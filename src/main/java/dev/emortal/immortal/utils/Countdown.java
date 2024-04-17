package dev.emortal.immortal.utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerFlag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Countdown {

    public static Task startCountdown(int seconds, Consumer<Integer> runnable, Runnable done) {
        return startCountdown(MinecraftServer.getSchedulerManager(), seconds, runnable, done);
    }

    public static Task startCountdown(Scheduler scheduler, int seconds, Consumer<Integer> runnable, Runnable done) {
        return scheduler.submitTask(new Supplier<>() {
            int secondsLeft = seconds;
            @Override
            public TaskSchedule get() {
                if (secondsLeft == 0) {
                    done.run();
                    return TaskSchedule.stop();
                }

                runnable.accept(secondsLeft);
                secondsLeft--;

                return TaskSchedule.tick(ServerFlag.SERVER_TICKS_PER_SECOND);
            }
        });
    }

    private Countdown() {

    }

}
