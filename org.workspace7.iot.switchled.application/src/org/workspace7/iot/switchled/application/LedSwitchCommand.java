
package org.workspace7.iot.switchled.application;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.debug.api.Debug;
import osgi.enroute.iot.gpio.api.CircuitBoard;
import osgi.enroute.iot.gpio.api.IC;
import osgi.enroute.iot.gpio.util.Digital;
import osgi.enroute.iot.gpio.util.ICAdapter;
import osgi.enroute.scheduler.api.Scheduler;

@Component(service = IC.class, property = {
	Debug.COMMAND_SCOPE + "=workspace7",
	Debug.COMMAND_FUNCTION + "=led"
})
public class LedSwitchCommand extends ICAdapter<Digital, Digital>
	implements Digital {

	public void led(boolean on) throws Exception {
		out().set(on);
	}

	@Override
	public void set(boolean value) throws Exception {
		if (value && _busy.getAndSet(true) == false) {

			_count = 10;

			_schedule = _scheduler.schedule(() -> {

				if (_count-- <= 0) {
					_busy.set(false);
					_schedule.close();
				}

				led((_count & 1) == 0);
			} , 500);
		}
	}

	@Reference
	protected void setCircuitBoard(CircuitBoard circuitBoard) {
		super.setCircuitBoard(circuitBoard);
	}

	@Reference
	protected void setScheduler(Scheduler scheduler) {
		_scheduler = scheduler;
	}

	private AtomicBoolean _busy = new AtomicBoolean();
	private int _count;
	private Scheduler _scheduler;
	private Closeable _schedule;

}
