/*******************************************************************************
 * Copyright (C) 2018 Anvar Sultanbekov
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package ru.navarobot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.ConvolutionalNetwork;
import org.neuroph.nnet.learning.MomentumBackpropagation;

public class NNet {

	public static void main(String[] args) {

		/*
		 * ConvolutionalNetwork neuralNet = new
		 * ConvolutionalNetwork.Builder().withInputLayer(64, 1, 1)
		 * .withFullConnectedLayer(30).withFullConnectedLayer(30).withFullConnectedLayer
		 * (4).build();
		 */

		ConvolutionalNetwork neuralNet;
		try {
			neuralNet = (ConvolutionalNetwork) NeuralNetwork
					.load(new FileInputStream(new File("/home/anvar/Desktop/Tanks/src/res/nnet/net.nnet")));
		} catch (FileNotFoundException e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		DataSet dataSet = new DataSet(64, 4);

		final Scanner scr;
		try {
			scr = new Scanner(new File("/home/anvar/Desktop/Tanks/src/res/nnet/train.dat"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		while (scr.hasNext()) {
			double[] input = new double[64];
			for (int i = 0; i < input.length; i++) {
				input[i] = scr.nextDouble();
			}
			dataSet.addRow(input,
					new double[] { scr.nextDouble(), scr.nextDouble(), scr.nextDouble(), scr.nextDouble() });
		}
		scr.close();

		boolean[] stop = new boolean[1];
		Thread keyboardListen = new Thread(new Runnable() {

			@Override
			public void run() {
				Scanner scr2 = new Scanner(System.in);
				while (!scr2.next().equals("s"))
					;
				stop[0] = true;
				scr2.close();
			}
		});
		keyboardListen.setDaemon(true);
		keyboardListen.start();

		neuralNet.getLearningRule().addListener(new LearningEventListener() {

			@Override
			public void handleLearningEvent(LearningEvent event) {
				System.out.println(((MomentumBackpropagation) event.getSource()).getPreviousEpochError());
				if (stop[0]) {
					neuralNet.stopLearning();
				}
			}
		});
		// ((MomentumBackpropagation)neuralNet.getLearningRule()).setMomentum(0.9);
		neuralNet.getLearningRule().setLearningRate(0.001);
		neuralNet.getLearningRule().setMaxError(0.00000001);
		// neuralNet.randomizeWeights(new SecureRandom());
		neuralNet.learn(dataSet);

		neuralNet.save("/home/anvar/Desktop/Tanks/src/res/nnet/net.nnet");

		/*
		 * neuralNet.setInput(1.348317265510559 ,0.43181878328323364
		 * ,1.1147205829620361, 2.4808125495910645 ,1.9902119636535645
		 * ,3.4930038452148438 ,1.9098176956176758, 4.860893249511719);
		 * neuralNet.calculate(); for (double num : neuralNet.getOutput()) {
		 * System.out.print(num + " "); } System.out.println();
		 */

	}

}
