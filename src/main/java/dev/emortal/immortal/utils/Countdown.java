package dev.emortal.immortal.utils;

import net.minestom.server.ServerFlag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Countdown {

    public static void startCountdown(Scheduler scheduler, int seconds, Consumer<Integer> runnable) {
        scheduler.submitTask(new Supplier<>() {
            int secondsLeft = seconds;
            @Override
            public TaskSchedule get() {
                if (secondsLeft == 0) {
                    return TaskSchedule.stop();
                }

                runnable.accept(secondsLeft);
                secondsLeft--;

                return TaskSchedule.tick(ServerFlag.SERVER_TICKS_PER_SECOND);
            }
        });
    }

}
