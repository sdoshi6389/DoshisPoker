import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
public class Files {
    // reads the file
    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> returnArray = new ArrayList<String>(); // blank array list

        try {
            File file = new File(fileName); // file from the input file name
            FileReader fr = new FileReader(file); // filereader
            BufferedReader br = new BufferedReader(fr); // buffered reader
            while (true) {
                String line = br.readLine(); // stores info in line
                if (line == null) { // if null, last line
                    break;
                } else {
                    returnArray.add(line); // if not null, adds that line to the arraylist
                }
            }
            br.close(); // closes the reader
            return returnArray; // returns array

        } catch (IOException e) {
            e.printStackTrace();
            return null; // if there was an error, returns null
        }
    }

    // writes a single line to a file
    public static boolean writeToFile(String fileName, String message) { // writes a single string to a file
        try {
            File file = new File(fileName); // new file
            FileOutputStream fos = new FileOutputStream(file, true); // to write to the file, appends
            PrintWriter pw = new PrintWriter(fos); // prints to the file
            pw.println(message); // this is the message printing
            pw.flush(); // flushes
            pw.close(); // closes
            return true; // returns true
        } catch (IOException e) {
            e.printStackTrace();
            return false; // something went wrong so returns false
        }
    }

    // writes an arraylist of objects to a file
    public static boolean writeFile(String fileName, ArrayList<String> fileContent) { // writes a bunch of stuff
        try {
            File file = new File(fileName); // new file
            FileWriter fw = new FileWriter(file); // to write to the file
            BufferedWriter bw = new BufferedWriter(fw); // buffers the writing
            PrintWriter print = new PrintWriter(bw); // prints to the file

            for (int i = 0; i < fileContent.size(); i++) {
                print.println(fileContent.get(i));
            }

            print.flush(); // flushes
            print.close(); // closes
            return true; // worked so returns true

        } catch (IOException e) {
            e.printStackTrace();
            return false; // something went wrong so returns false
        }
    }

    // writes a file
    public static boolean writeFile(String fileName) {
        File file = new File(fileName); // new file
        try {
            FileWriter fw = new FileWriter(file); // to write to the file
            BufferedWriter bw = new BufferedWriter(fw); // buffers the writing
            PrintWriter print = new PrintWriter(bw); // prints to the file

            print.flush(); // flushes
            print.close(); // closes
            return true; // worked so returns true

        } catch (IOException e) {
            e.printStackTrace();
            return false; // something went wrong so returns false
        }
    }

    // find a line in the file and returns a split static array of that
    public static String[] findLine(String filename, String first) {
        ArrayList<String[]> data = splitData(filename);
        String[] returnString = new String[0];
        boolean found = false;

        for (int i = 0; i < data.size(); i++) {
            String[] current = data.get(i);
            if (current[0].equals(first)) {
                returnString = current;
                found = true;
            }
        }

        if (!found) {
            return null;
        } else {
            return returnString;
        }
    }

    // splits the data and returns an arraylist of split static arrays
    public static ArrayList<String[]> splitData(String filename) {
        ArrayList<String> array = readFile(filename);
        ArrayList<String[]> returnArray = new ArrayList<String[]>();

        for (int i = 0; i < array.size(); i++) {
            String current = array.get(i);
            String[] split = current.split(",");

            returnArray.add(split);
        }
        return returnArray;
    }

    public static String[] splitData(ArrayList<String> split) {
        String[] returnData;

        int length = split.size();
        returnData = new String[length];

        for (int i = 0; i < split.size();  i++) {
            returnData[i] = split.get(i);
        }

        return returnData;
    }

    // deletes line
    public static boolean deleteLine(String line, String fileName) {
        File f = new File(fileName);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);


            ArrayList<String> fileContent = new ArrayList<>();


            String fileLine = bfr.readLine();


            while (fileLine != null) {
                fileContent.add(fileLine);
                fileLine = bfr.readLine();
            }
            bfr.close();


            if (!f.delete()) {
                throw new RuntimeException();
            }


            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).equals(line)) {
                    fileContent.remove(i);
                }
            }


            File newFile = new File(fileName);
            FileWriter fw = new FileWriter(newFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);


            for (int j = 0; j < fileContent.size(); j++) {
                pw.println(fileContent.get(j));
            }
            pw.flush();
            pw.close();


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}