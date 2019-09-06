package com;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class RetrieveBalance {

    private static CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\r\n").withNullString("");
    private String fileLocation = "<File-Location>";
    private String fileName = "Transactions.csv";

    public static int transactionId_pos = 0;
    public static int fromAccountId_pos = 1;
    public static int toAccountId_pos = 2;
    public static int createdAt_pos = 3;
    public static int amount_pos = 4;
    public static int transactionType_pos = 5;
    public static int relatedTransaction_pos = 6;

    public void run(String args[]){
        if (validateConfigExists(fileLocation)) {
            calculateBalance(args);
        } else {
            System.err.println("Transaction file doesnt exist");
        }
    }

    public void calculateBalance(String args[]){
        float balance = 0;
        int txCount = 0;
        CSVParser csv = null;
        DateFormat formatter;
        String inputAccount = args[0];
        String transactionStartDate = args[1];
        String transactionEndDate = args[2];

        try {

            csv = CSVParser.parse(new File(fileLocation, fileName), Charset.defaultCharset(), csvFileFormat);

            for (CSVRecord csvRecord : csv) {
//                System.out.println(csvRecord.get(fromAccountId_pos) + "," + inputAccount + "," + csvRecord.size());
                formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date createAt = formatter.parse(csvRecord.get(createdAt_pos).trim());
                java.sql.Timestamp txnTime = new Timestamp(createAt.getTime());
                Date startDate = formatter.parse(transactionStartDate);
                java.sql.Timestamp startTimestamp = new Timestamp(startDate.getTime());
                Date endDate = formatter.parse(transactionEndDate);
                java.sql.Timestamp endTimestamp = new Timestamp(endDate.getTime());
//                System.out.println(txnTime + ","+ startTimestamp + "," + endTimestamp);
                if (csvRecord.get(fromAccountId_pos).trim().equals(inputAccount)) { // Debit from account
                    if (startTimestamp.compareTo(txnTime) < 0 && endTimestamp.compareTo(txnTime) > 0) {
                        if (csvRecord.get(transactionType_pos).trim().equals("PAYMENT")) {
                            balance = balance - Float.parseFloat(csvRecord.get(amount_pos).trim());
                            txCount++;
                        }
                        System.out.println(balance + "," + txCount);
                    }
                    if (csvRecord.get(transactionType_pos).trim().equals("REVERSAL")) {
                        balance = balance + Float.parseFloat(csvRecord.get(amount_pos).trim());
                        txCount--;
                    }
                    System.out.println("Debit : " + balance + "," + txCount);
                }
                if (csvRecord.get(toAccountId_pos).trim().equals(inputAccount)) { // Credit to account

                    if (startTimestamp.compareTo(txnTime) < 0 && endTimestamp.compareTo(txnTime) > 0) {
                        if (csvRecord.get(transactionType_pos).trim().equals("PAYMENT")) {
                            balance = balance + Float.parseFloat(csvRecord.get(amount_pos).trim());
                            txCount++;
                        }
                        System.out.println(balance + "," + txCount);
                    }
                    if (csvRecord.get(transactionType_pos).trim().equals("REVERSAL")) {
                        balance = balance - Float.parseFloat(csvRecord.get(amount_pos).trim());
                        txCount--;
                    }
                    System.out.println("Credit : " + balance + "," + txCount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Final Balance : " + balance);
        System.out.println("Number of transactions :" + txCount);
    }

    public boolean validateConfigExists(String fileLocation) {
        File configFile = new File(fileLocation, fileName);
        return configFile.exists();
    }
}