package mas.localSchedulingproxy.algorithm;

import java.util.ArrayList;

import mas.jobproxy.Batch;

public interface ScheduleSequenceIFace {

	public ArrayList<Batch> solve();
}
