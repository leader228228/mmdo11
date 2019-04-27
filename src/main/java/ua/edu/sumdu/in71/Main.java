package ua.edu.sumdu.in71;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    /**
     *  The matrix we read must have must contain the information about how much profit we will get if invest nothing,
     *  i.e. the first number of the row is considered as the gain from 0.
     *  Also it must have the same number of elements in each row.
     * */
    public static void main(String[] args) {
        double [][] matrix;
        try {
            matrix = input();
        } catch (FileNotFoundException e) {
            if (LOGGER.isEnabledFor(Level.ERROR)) {
                LOGGER.error("Unable to find the file specified while input", e);
            }
            return;
        } catch (IOException e) {
            if (LOGGER.isEnabledFor(Level.ERROR)) {
                LOGGER.error(e);
            }
            return;
        }
        if (!validate(matrix)) {
            if (LOGGER.isEnabledFor(Level.ERROR)) {
                LOGGER.error("The input matrix is not valid");
            }
            printMatrix(matrix);
            return;
        }
        Node node = new Node(matrix[0].length - 1);
        DynamicProgrammingSolution.buildMostProfitDistributionPlan(node, 0, new DiscreteDistribution() {
            private double [][] m = matrix;
            @Override
            public double getProfit(int whom, int portionOfInvestments) {
                try {
                    return m[whom][portionOfInvestments];
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException();
                }
            }

            @Override
            public double getAmOfUnits() {
                return m.length;
            }
        });
        printResult(node);
    }

    private static void printResult(Node node) {
        StringBuilder stringBuilder = new StringBuilder(System.lineSeparator());
        int unit = 0;
        double totalProfit = 0;
        while (node != null) {
            stringBuilder.append("Invest ")
                    .append(node.getInvestment())
                    .append(" to the ")
                    .append(unit++)
                    .append(" unit |=> +")
                    .append(node.getProfit())
                    .append(System.lineSeparator());
            totalProfit += node.getProfit();
            node = node.getNext();
        }
        stringBuilder.append("Total profit is " + totalProfit);
        if (LOGGER.isEnabledFor(Level.INFO)) {
            LOGGER.info(stringBuilder.toString());
        }
    }

    private static boolean validate(double [][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        if (matrix.length == 1) {
            return true;
        }
        int length = matrix[0].length;
        for (double [] row : matrix) {
            if (row.length != length) {
                return false;
            }
        }
        return true;
    }

    private static double [][] input() throws IOException {
        Scanner scanner = new Scanner(System.in);
        LOGGER.info("Discrete distribution file:");
        File file = new File(scanner.nextLine());
        return readInputMatrix(file);
    }

    private static void printMatrix(double [][] d) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                stringBuilder.append(d[i][j]).append(' ');
            }
            stringBuilder.append(System.lineSeparator());
        }
        if (LOGGER.isEnabledFor(Level.TRACE)) {
            LOGGER.trace(System.lineSeparator() + stringBuilder);
        }
    }

    private static double [][] readInputMatrix(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        List<String> discreteDistributions = new LinkedList<>();
        while (scanner.hasNextLine()) {
            discreteDistributions.add(scanner.nextLine());
        }
        List<double[]> distributions = new LinkedList<>();
        for (int i = 0; i < discreteDistributions.size(); i++) {
            String [] row = discreteDistributions.get(i).split(" ");
            double [] r = new double[row.length];
            for (int j = 0; j < row.length; j++) {
                r[j] = Double.valueOf(row[j]);
            }
            distributions.add(r);
        }
        double [][] result;
        try {
            result = new double[distributions.size()][distributions.get(0).length];
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("Empty input");
        }
        for (int i = 0; i < distributions.size(); i++) {
            for (int j = 0; j < distributions.get(i).length; j++) {
                result[i][j] = distributions.get(i)[j];
            }
        }
        return result;
    }

    /**
     *  Returns the total profit
     * */
    private static double printSolution(Node node) {
        int step = 0;
        double totalProfit = 0;
        while (node != null) {
            System.out.println(step++ + " unit receives " + node.getInvestment() + ", income +" + node.getProfit());
            totalProfit += node.getProfit();
            node = node.getNext();
        }
        return totalProfit;
    }

    // TEST
    private static void varControl() {
        DiscreteDistribution discreteDistribution = new DiscreteDistribution() {
            @Override
            public double getProfit(int whom, int portionOfInvestments) {
                double [][] distribution = new double[][]{
                        {0, 2.2, 3, 4.1, 5.2, 5.9},
                        {0, 2, 3.2, 4.8, 6.2, 6.4},
                        {0, 2.8, 5.4, 6.4, 6.6, 6.9}
                };
                try {
                    return distribution[whom][portionOfInvestments];
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException();
                }
            }

            @Override
            public double getAmOfUnits() {
                return 3;
            }
        };
        Node node = new Node(5);
        DynamicProgrammingSolution.buildMostProfitDistributionPlan(node, 0, discreteDistribution);
        printSolution(node);
    }

    // TEST
    private static void var27() {
        DiscreteDistribution discreteDistribution = new DiscreteDistribution() {
            @Override
            public double getProfit(int whom, int portionOfInvestments) {
                double [][] distribution = new double[][]{
                        {0, 4.4, 4.7, 4.9, 5, 5.2},
                        {0, 4.7, 4.9, 5.4, 5.4, 6.4},
                        {0, 4, 4.5, 5.1, 6, 6.2}
                };
                try {
                    return distribution[whom][portionOfInvestments];
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException();
                }
            }

            @Override
            public double getAmOfUnits() {
                return 3;
            }
        };
        Node node = new Node(5);
        DynamicProgrammingSolution.buildMostProfitDistributionPlan(node, 0, discreteDistribution);
        printSolution(node);
    }
}
