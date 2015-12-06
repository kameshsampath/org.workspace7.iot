/**
* Copyright 2015-present Kamesh Sampath<kamesh.sampath@hotmail.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.workspace7.iot.led.application.command;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import osgi.enroute.scheduler.api.Scheduler;

/**
 * @author Kamesh Sampath
 */
@Component
public class LedSwitchCommand {

	@Activate
	void acivate() throws Exception {
		System.out.println("RadheKrishna!");

		_gpioController = GpioFactory.getInstance();

		final GpioPinDigitalOutput redPin = _provisoRed();
		_outputPins.add(redPin);

		final GpioPinDigitalOutput amberPin = _provisoAmber();
		_outputPins.add(amberPin);

		final GpioPinDigitalOutput greenPin = _provisoGreen();
		_outputPins.add(greenPin);

		_scheduler.schedule(() -> {
			redPin.pulse(300, true);
			amberPin.pulse(300, true);
			greenPin.pulse(300, true);
		} , 500);

	}

	private GpioPinDigitalOutput _provisoRed() throws Exception {
		final GpioPinDigitalOutput pin =
			_gpioController.provisionDigitalOutputPin(
				RED_PIN, "RED_LED", PinState.LOW);
		pin.setShutdownOptions(true, PinState.LOW);
		return pin;
	}

	private GpioPinDigitalOutput _provisoAmber() throws Exception {
		final GpioPinDigitalOutput pin =
			_gpioController.provisionDigitalOutputPin(
				AMBER_PIN, "AMBER_LED", PinState.LOW);
		pin.setShutdownOptions(true, PinState.LOW);
		return pin;
	}

	private GpioPinDigitalOutput _provisoGreen() throws Exception {
		final GpioPinDigitalOutput pin =
			_gpioController.provisionDigitalOutputPin(
				GREEN_PIN, "GREEN_LED", PinState.LOW);
		pin.setShutdownOptions(true, PinState.LOW);
		return pin;
	}

	@Deactivate
	void deactivate() throws Exception {

		System.out.println("Haribol!");

		_schedule.close();

		for (GpioPinDigitalOutput pin : _outputPins) {
			_gpioController.unprovisionPin(pin);
		}

		// _gpioController.shutdown();
	}

	@Reference
	void setScheduler(Scheduler scheduler) {
		this._scheduler = scheduler;
	}

	private static final Pin RED_PIN = RaspiPin.GPIO_00;
	private static final Pin AMBER_PIN = RaspiPin.GPIO_01;
	private static final Pin GREEN_PIN = RaspiPin.GPIO_02;
	private GpioController _gpioController;
	private List<GpioPinDigitalOutput> _outputPins = new ArrayList<>();
	private Scheduler _scheduler;
	private Closeable _schedule;
}
