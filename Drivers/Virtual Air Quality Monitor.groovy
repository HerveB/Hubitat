/**
 *  Virtual Air Quality Monitor
 *
 *  This is an empty shell to be feed howevery you want from any source (or coallated sources)
 *    [My personal usage:
 *        Source: RPi Zero W to collate an attached I2C PM2.5 sensor and bluetooth data from an Airthings Wave Plus for T, H, P, CO2, TVOC, and both Radon avgs
 *        Feed: That same RPi is using the MakerAPI to populate these fields (I have no other consumers to justify MQTT overhead)]
 *  Copyright 2020 LostJen
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 */
metadata {
	definition (name: "Virtual Air Quality Monitor", namespace: "LostJen", author: "LostJen") {
        capability "Sensor"
        capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
        capability "Pressure Measurement"
        capability "Carbon Dioxide Measurement"
     
        attribute "pm2_5", "number"
        attribute "tVOC", "number"
        attribute "radonShortTermAvg", "number"
        attribute "radonLongTermAvg", "number"       
        attribute "lastUpdateWavePlus", "date"       
        attribute "lastUpdatePM2_5", "date"       

	preferences {
		input name: "useF", type: "bool", title: "Use Imperial (F) instead of Metric (C)", required: true, defaultValue: false
	}
        command "setValuesNoPM2_5", [[type:"STRING"],[type:"STRING"],[type:"STRING"],[type:"STRING"],[type:"STRING"],[type:"STRING"],[type  :"STRING"]]
        command "setValuePM2_5", [[type:"STRING"]]
        command "errorNotFound", []
	}
}

def errorNotFound()
{
    log.error("WavePlus not found via BlueTooth")
}

def setValuePM2_5(String pm2_5)
{
    sendEvent(name: "pm2_5", value: pm2_5, unit: "µg/m³", isStateChange: true)
    sendEvent(name: "lastUpdatePM2_5", value: new Date(now()), isStateChange: true)
}

def setValuesNoPM2_5(String temp, String rh, String bar, String co2, String tVoc, String radonShortTermAvg, String radonLongTermAvg)
{
    modifiedTemp = temp.toDouble()
    if (useF) modifiedTemp = (modifiedTemp * 1.8) + 32
    sendEvent(name: "temperature", value: modifiedTemp.round(2), unit: "°", isStateChange: true)
    sendEvent(name: "humidity", value: rh.toDouble().round(0), unit: "%", isStateChange: true)
    sendEvent(name: "pressure", value: bar, unit: "mbar", isStateChange: true)
    sendEvent(name: "carbonDioxide", value: co2, unit: "ppm", isStateChange: true)
    sendEvent(name: "tVOC", value: tVoc, unit: "ppb", isStateChange: true)
    sendEvent(name: "radonShortTermAvg", value: radonShortTermAvg.toDouble().round(2), unit: "pCi/L", isStateChange: true)
    sendEvent(name: "radonLongTermAvg", value: radonLongTermAvg.toDouble().round(2), unit: "pCi/L", isStateChange: true)
    sendEvent(name: "lastUpdateWavePlus", value: new Date(now()), isStateChange: true)
}
