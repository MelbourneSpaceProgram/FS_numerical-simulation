/* Copyright 20017-2018 Melbourne Space Program
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This package gathers all of the objects responsible in
 * the propagation, i.e. the stage where a state  
 * defined at the time T is brought to the next time T + dT
 * through updates and integrations of the different variables.
 * 
 *  @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */package msp.simulator.dynamic.propagation;
 
 
 /**
 * READ ME
 * Note about the notion of step handling. (cf. StepHandler.java and 
 * master mode of a propagator.)
 * The step handling is designed to have, at the end of each finalized
 * step, a custom processing designed by the user.
 * This is a part of the integration method and a part of Hipparchus.
 * Then OreKit wraps it around a space dynamic view but the concept
 * is the same.
 * This note is a warning and an reminder for future usage.
 * <p>
 * Let be a state at the time t: s(t).
 * Indeed, we could easily thing that this function is called once the 
 * different integrations and updates of the state variables are over.
 * And then, with the new updated state s(t+dt) at its disposal, the
 * step handling can happen.
 * BUT THIS IS WRONG.
 * Actually, once the integrations and the updates are done, the event
 * handling occurs (depending also on the previous state s(t)) and,
 * if no event occurs or the relevant action is to continue, then
 * the step handler is called with s(t)! And that makes sense.
 * Because this custom function will take care, in its own way, to bring
 * the state s(t) at the new date t+dt.
 * But the whole point to remember, on my opinion, is that we cannot think 
 * of using the new integrated variables in there, for an attitude propagation
 * for instance.
 * Finally the returned state of the propagation function will be s(t+dt).
 * 
 */