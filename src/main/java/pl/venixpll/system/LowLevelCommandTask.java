package pl.venixpll.system;

import pl.venixpll.utils.LogUtil;

import java.util.Scanner;

public class LowLevelCommandTask implements Runnable {
    @Override
    public void run() {
        final Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            final String input = scanner.nextLine();
            if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")){
                LogUtil.printMessage("Stopping...");
                System.exit(0);
            }
        }
    }
}
