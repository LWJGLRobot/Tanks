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
package ru.navarobot;

import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public enum Audio {

	LASER("lazer.wav"), MISSILE("missile.wav"), GAMESTART("gameStart.wav"), POP("pop.wav"), FUSE("fuse.wav"),
	BOOM("boom.aiff"), COLLECT("collect.wav"), BUBBLE("bubble.aiff"), WIN("win.wav");

	public final Media media;

	public static final int MAX_DURATION_MILLIS = 3000;

	Audio(String name) {
		media = new Media(ClassLoader.getSystemResource("res/audio/" + name).toString());
	}

	public void play() {
		MediaPlayer player = new MediaPlayer(media);
		player.play();
		new Timer(true).schedule(new TimerTask() {

			@Override
			public void run() {
				player.stop();
				player.dispose();
			}
		}, Double.isFinite(media.getDuration().toMillis()) ? (long) media.getDuration().toMillis()
				: MAX_DURATION_MILLIS);
	}
}
