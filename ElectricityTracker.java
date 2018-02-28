import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ArrayList;;

public class ElectricityTracker {

    public static void main(String[] args) {
        String fileName = "Electricity.ics";
        String line = null;
        Float balance = null;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            boolean updateFound = false;
            System.out.println("Date Range\t\t\tDaily Average\tMonth Trend\tYear Trend");
            ArrayList<Entry> entries = new ArrayList<Entry>();
            while((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("SUMMARY")){
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
                        Entry entry = new Entry(balance, date);
                        entries.add(entry);
                    } catch (Exception e) {

                    }
                    updateFound = false;
                }
            }


            Collections.sort(entries, new SortByDate());

            float totalSpend = 40;

            for (int i = 1; i < entries.size(); i++){
                Entry previousEntry = entries.get(i - 1);
                Entry currentEntry = entries.get(i);
                long timeDifference = currentEntry.date.getTime() - previousEntry.date.getTime();
                int days = (int) Math.ceil(timeDifference / 1000 / 60 / 60 / 24);
                float actualPrevBalance = previousEntry.value;
                if (previousEntry.value < currentEntry.value || days > 21) {
                    actualPrevBalance += 40;
                    totalSpend += 40;
                }
                float balanceDifference = actualPrevBalance - currentEntry.value;
                float dailyAverage = balanceDifference / days;
                float annualAverage = dailyAverage * 365;
                float monthlyAverage = annualAverage / 12;
                System.out.println(
                    new SimpleDateFormat("dd/MM/yyyy").format(previousEntry.date) + " - " + 
                    new SimpleDateFormat("dd/MM/yyyy").format(currentEntry.date) + 
                    "\t\t£" + String.format("%.2f", dailyAverage) +
                    "\t\t£" + String.format("%.2f", monthlyAverage) +
                    "\t\t£" + String.format("%.2f", annualAverage)
                );
            }

            System.out.println("Total Spent: £" + String.format("%.2f", totalSpend));
            bufferedReader.close();         
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find file: " + fileName);                
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
    }
}

class Entry {
    public Float value;
    public Date date;

    public Entry(Float value, Date date) {
        this.value = value;
        this.date = date;
    }
}

class SortByDate implements Comparator<Entry>{
    public int compare(Entry a, Entry b) {
        int result = 1;
        if (a.date.before(b.date)) {
            result = -1;
        }
        return result;
    }
}