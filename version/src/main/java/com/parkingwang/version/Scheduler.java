package com.parkingwang.version;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public interface Scheduler {

    void submit(Runnable task);

    void shutdown();

    class NewThread implements Scheduler {

        public static NewThread create(){
            return new NewThread();
        }

        @Override
        public void submit(Runnable task) {
            new Thread(task).start();
        }

        @Override
        public void shutdown() {
            // NOP
        }
    }

}
