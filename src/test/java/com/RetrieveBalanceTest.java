package com;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class RetrieveBalanceTest {

    RetrieveBalance tester = new RetrieveBalance();

    @Test
    public void testRun() throws IOException {
        String[] args = {"ACC334455","20/10/2018 12:00:00", "20/10/2018 19:00:00"};
        tester.run(args);
    }

    @Test
    public void testCalculate() throws IOException {
        String[] args = {"ACC334455","20/10/2018 12:00:00", "20/10/2018 19:00:00"};
        tester.run(args);
    }
}