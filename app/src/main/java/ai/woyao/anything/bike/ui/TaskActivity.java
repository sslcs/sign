package ai.woyao.anything.bike.ui;


import java.util.ArrayList;

import ai.woyao.anything.bike.net.NetTask;

public class TaskActivity extends BaseActivity {
    private ArrayList<NetTask> tasks;

    /**
     * 添加请求任务
     *
     * @param task 请求任务
     */
    public void addTask(NetTask task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tasks != null) {
            for (NetTask task : tasks) {
                if (task != null) task.cancel();
            }
        }
    }
}
