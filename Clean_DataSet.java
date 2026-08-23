package src;

import java.util.*;
import java.io.*;

public class Clean_DataSet {
    public static void main(String[] args) {
        File dataset = new File("src/dfTransjakarta.csv");
        File outputFile = new File("src/cleanTransportationData.csv");
        ArrayList<String> headers = new ArrayList<>();
        String tempLine;
        String[] lineValues;
        try{
            BufferedReader fileReader = new BufferedReader(new FileReader(dataset));
            FileWriter outputWriter = new FileWriter(outputFile);


            tempLine = fileReader.readLine();
            lineValues = tempLine.split(",");


            /**
             * Extracting the columns:
             * - corridorName
             * - direction
             * - tapInStopName
             * - tapOutStopName
             * - payCardBank
             * - payCardSex
             */
            outputWriter.write(lineValues[7]+","+lineValues[8]+","  + lineValues[10]+"," + lineValues[16]+"," +lineValues[2] +","+ lineValues[4]+ "\n");


           tempLine = fileReader.readLine();

            //int rowCounter = 0;
            //&& rowCounter < 100
            while (tempLine != null  ){

                if (tempLine.contains("\"")){
                    tempLine = fileReader.readLine();
                    continue;
                }

                lineValues = tempLine.split(",");

                //Skipping the rows with missing columns
                if (lineValues.length > 21) {
                    if (!lineValues[7].isEmpty() && !lineValues[8].isEmpty() && !lineValues[10].isEmpty() && !lineValues[16].isEmpty() && !lineValues[2].isEmpty()&& !lineValues[4].isEmpty() ) {

                        outputWriter.write(lineValues[7] + "," + lineValues[8] + "," + lineValues[10] + "," + lineValues[16] + "," + lineValues[2] + "," + lineValues[4] + "\n");
                    }

                }


                tempLine = fileReader.readLine();
                //rowCounter++;
            }

            fileReader.close();
            outputWriter.close();
            System.out.println("Done");

        }catch (IOException e){
            System.out.println("Can NOT find the original data set: \n" + e);
        }


    }
}
