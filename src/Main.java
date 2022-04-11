import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String [] args) {

        System.out.println("Enter the k value: ");
        Scanner scanner = new Scanner(System.in);
        int k = scanner.nextInt();


        String trainingSetPath = "data.txt";
        String testSetPath = "testdata.txt";

        List<Classifier> trainSetList = new ArrayList<>();
        List<Classifier> testSetList = new ArrayList<>();

        //loading training set

        String[] line_Training;
        List<String> fileLines_Training;
        double [] vector;
        String [] vectorS;
        String type;
        try{
            fileLines_Training = new BufferedReader(new FileReader(trainingSetPath))
                    .lines()
                    .collect(Collectors.toList());
            for(int i = 0; i < fileLines_Training.size(); i++) {
                line_Training = fileLines_Training.get(i).split(",");
                type = line_Training[line_Training.length - 1];
                vectorS = Arrays.copyOfRange(line_Training,0,line_Training.length - 1);
                vector = Arrays.stream(vectorS).mapToDouble(Double::parseDouble).toArray();
                Classifier classifier = new Classifier(vector,type);
                trainSetList.add(classifier);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //loading test set

        String[] line_Test;
        List<String> fileLines_Test;
        double [] vector1;
        String [] vector1S;
        String type1;
        int vectorSize = 0;

        try{
            fileLines_Test = new BufferedReader(new FileReader(testSetPath))
                    .lines()
                    .collect(Collectors.toList());
            for (int i = 0; i < fileLines_Test.size(); i++) {
                line_Test = fileLines_Test.get(i).split(",");
                type1 = line_Test[line_Test.length - 1];
                vector1S = Arrays.copyOfRange(line_Test,0,line_Test.length - 1);
                vector1 = Arrays.stream(vector1S).mapToDouble(Double::parseDouble).toArray();
                Classifier classifier = new Classifier(vector1,type1);
                testSetList.add(classifier);
                vectorSize = vector1.length;
            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("What operation would you like to perform?");
        System.out.println("1 - analyze train set");
        System.out.println("2 - enter my own vector");
        int choice = 0;

        choice = scanner.nextInt();

        if(choice == 1) {

            int counter = 0;
            int correct = 0;

            for (int i = 0; i < testSetList.size(); i++) {
                Classifier classifier = testSetList.get(i);
                String classifierType = testSetList.get(i).type;
                String classifiedAs = classify(classifier, testSetList,k);
                counter++;
                if(classifierType == classifiedAs) {
                    correct++;
                }
                System.out.println("-------------------------------------------------------------------------------");

            }

            double acc = correct / counter * 100;
            System.out.println("Accuracy was: " + acc + "%");


        }else if(choice == 2) {
            List<String> vectorList = new ArrayList<>();
            System.out.println("Enter your vector");
            for(int i = 0; i < vectorSize; i++) {
                vectorList.add(scanner.next());
            }
            String [] array = vectorList.toArray(new String[0]);
            double [] userVector = Arrays.stream(array).mapToDouble(Double::parseDouble).toArray();
            Classifier userClassifier = new Classifier(userVector);
            classify(userClassifier,trainSetList,k);
        }
    }

    public static String classify(Classifier classifier, List<Classifier> list, int limit) {

        String testType = classifier.type;
        double [] testVector = classifier.vector;
        HashMap<Double, String> distancesMap = new HashMap<>();
        HashMap<Double, String> sortedDistances = new HashMap<>();


        for(int i = 0; i < list.size(); i++) {
            String trainType = list.get(i).type;
            double [] trainVector = list.get(i).vector;
            double distance = 0;
            for (int j = 0; j < testVector.length; j++) {
                distance += Math.sqrt(Math.pow(testVector[j] - trainVector[j],2));
            }
            distancesMap.put(distance,trainType);
        }

        sortedDistances = distancesMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(oldValue,newValue) -> oldValue, LinkedHashMap::new));

        System.out.println("test type was: " + testType);
        sortedDistances.forEach((k,v) -> System.out.println(k + " " + v));

        List<String> classesList = new ArrayList<>();

        for(Map.Entry<Double,String> entry : sortedDistances.entrySet()) {
            classesList.add(entry.getValue());
        }

        Map<String, Integer> stringsCount = new HashMap<>();
        for(String s : classesList) {
            Integer c = stringsCount.get(s);
            if(c == null)
                c = new Integer(0);
            c++;
            stringsCount.put(s,c);
        }

        Map.Entry<String, Integer> mostRepeated = null;
        for(Map.Entry<String,Integer> e : stringsCount.entrySet()) {
            if(mostRepeated == null || mostRepeated.getValue() < e.getValue())
                mostRepeated = e;
        }

        String myClass = "";

        if(mostRepeated != null)
            myClass = mostRepeated.getKey();

        System.out.println("Classified as: " + myClass);

        return myClass;


    }



}
