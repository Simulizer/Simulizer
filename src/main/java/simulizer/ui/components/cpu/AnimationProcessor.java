package simulizer.ui.components.cpu;

import javafx.concurrent.Task;
import simulizer.ui.components.CPU;
import simulizer.ui.components.cpu.listeners.CPUListener;
import simulizer.ui.windows.CPUVisualisation;
import simulizer.utils.ThreadUtils;

import java.util.concurrent.*;

public class AnimationProcessor {

    class Animation{

        double time;
        Task animation;

        public Animation(double time, Task animation){
            this.time = time;
            this.animation = animation;
        }

        public double getTime(){ return time; }
        public Task getTask(){ return animation; }

    }


    public ScheduledExecutorService executorService;
    public ScheduledFuture executorTask;
    public BlockingQueue<Animation> animationTasks;
    public CPUListener cpuListener;
    public CPU cpuVisualisation;
    public int timeToNextAnimation;
    public int dispatchTime;
    public boolean showingWarning;

    public AnimationProcessor(CPU cpuVisualisation){
        this.cpuVisualisation = cpuVisualisation;
        dispatchTime = 20;
        timeToNextAnimation = 0;
        showingWarning = false;
        animationTasks = new LinkedBlockingQueue<>();
        executorService = Executors.newSingleThreadScheduledExecutor(
                new ThreadUtils.NamedThreadFactory("CPU-Visualisation-Job-Dispatch"));
        executorTask = executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(animationTasks.size() > 0){
                    // Are there animation jobs?
                    if(cpuListener.getSimCpu().getCycleFreq() > 2){
                        animationTasks.clear();
                        if(!showingWarning) {
                            new Thread(new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    cpuVisualisation.showText("Please lower the clock speed to less than 2Hz to see animations", 1000, false);
                                    showingWarning = true;
                                    return null;
                                }
                            }).start();
                        }
                        return;
                    }

                    showingWarning = false;

                    if(animationTasks.size() > 10){
                        // too many animations, remove some
                        for(int i = 0; i < animationTasks.size() / 2; i++){
                            animationTasks.poll();
                        }
                    }
                    if(timeToNextAnimation == 0){
                        Animation animation = animationTasks.poll();
                        new Thread(animation.getTask()).start();
                        timeToNextAnimation += animation.getTime();
                    }

                    timeToNextAnimation -= dispatchTime;
                    if(timeToNextAnimation < 0) timeToNextAnimation = 0;

                }


            }
        }, 0, dispatchTime, TimeUnit.MILLISECONDS);

    }

    public void setCpuListener(CPUListener cpuListener){
        this.cpuListener = cpuListener;
    }

    public int getRemaining(){
        return animationTasks.size();
    }

    public void shutdown(){
        executorTask.cancel(true);
        executorService.shutdownNow();
    }

    public void addAnimationTask(Task task, double speed){
        animationTasks.add(new Animation(speed, task));
    }
}
