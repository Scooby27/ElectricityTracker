import java.io.*;
import java.text.SimpleDateFormat;  
import java.util.Date;  

public class ElectricityTracker {

	public static void main(String[] args) {
        String fileName = "Electricity.ics";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            boolean updateFound = false;
            float balance = -1;
            float previousBalance = -1;
            String previousParsedDate = "";
            Date previousDate = null;
            float totalSpent = 40;
            System.out.println("Date Range\t\t\tDaily Average\tMonth Trend\tYear Trend");
            while((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("SUMMARY")){
                    previousBalance = balance;
                    balance = Float.parseFloat(line.split("£")[1]);
                    updateFound = true;
                } else if (updateFound) {
                    String timestamp = line.split(":")[1];
                    String year = timestamp.substring(0, 4);
                    String month = timestamp.substring(4, 6);
                    String day = timestamp.substring(6, 8);
                    String parsedDate = day + "/" + month + "/" + year;
                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(parsedDate);  
                        if (previousDate != null) {
                            long timeDifference = date.getTime() - previousDate.getTime();
                            int days = (int) Math.ceil(timeDifference / 1000 / 60 / 60 / 24);
                            float actualPrevBalance = previousBalance;
                            if (previousBalance < balance) {
                                actualPrevBalance = previousBalance + 40;
                                totalSpent += 40;
                            }
                            float balanceDifference = actualPrevBalance - balance;
                            float dailyAverage = balanceDifference / days;
                            float annualAverage = dailyAverage * 365;
                            float monthlyAverage = annualAverage / 12;
                            System.out.println(
                                previousParsedDate + " - " + parsedDate + 
                                "\t\t£" + String.format("%.2f", dailyAverage) +
                                "\t\t£" + String.format("%.2f", monthlyAverage) +
                                "\t\t£" + String.format("%.2f", annualAverage)
                            );
                        }
                        previousParsedDate = parsedDate;
                        previousDate = date;
                        updateFound = false;
                    } catch (Exception ex) {

                    }
                }
            }
            System.out.println("Total Spent: £" + String.format("%.2f", totalSpent));
            bufferedReader.close();         
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find file: " + fileName);                
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
	}

}