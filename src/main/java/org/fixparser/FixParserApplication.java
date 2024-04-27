package org.fixparser;

import org.fixparser.component.FixComponent;
import org.fixparser.service.FixParserService;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

public class FixParserApplication {
    public static void main(String[] args) {

        try {
            String input_D = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000156=ABROKER\u000134=2\u000152=2003061501:14:49\u000111=12345\u00011=111111\u000163=0\u000164=20030621\u000121=3\u0001110=1000\u0001111=50000\u000155=IBM\u000148=459200101\u000122=1\u000154=1\u000160=20030615-01:14:49\u000138=5000\u000140=1\u000144=15.75\u000115=USD\u000159=0\u000110=127\u0001";
            String input_F = "8=FIX.4.2\u00019=122\u000135=F\u000134=1\u000149=SenderCompID\u000156=TargetCompID\u000141=origClOrdID\u000160=20240101-10:00:00\u000111=OrderID123\u000155=MSFT\u000154=1\u000138=100\u000140=1\u000159=0\u000110=232\u0001";

            long startTime = System.nanoTime();
            long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            // ***** Example to call the FIX parser
            FixComponent msg = FixParserService.parseGeneric(input_D.getBytes());
            //FixComponent msg = FixParserService.parseByMessageType(input_F.getBytes());

            // ***** Getting some benchmarks
            long usedMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() +
                    ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
            System.out.println("Execution time: " + (System.nanoTime() - startTime) / 1000 + " ms;" +
                    "Memory used (runtime): " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryBefore) + " bytes; " +
                    "Memory used (MemoryMXBean): " + usedMemory + " bytes; ");
            System.out.println("-------------------------------------------");

            // ***** Example to get value by tag and to display the FIX object
            byte[] b;
            for(int tag : Arrays.asList(49, 56, 109, 9, 60)) {
                b = msg.getValueByTag(tag);
                if(b != null)
                    System.out.println(String.format("Tag %s: ", tag) + new String(b));
            }
            System.out.println("-------------------------------------------");
            System.out.println(msg);

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}