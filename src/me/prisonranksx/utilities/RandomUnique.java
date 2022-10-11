package me.prisonranksx.utilities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

/**
 * 
 * Generate random unique numbers
 *
 */
public class RandomUnique {

	private static final int DEFAULT_LIMIT = 10;
	private static final RandomUnique GLOBAL = new RandomUnique(DEFAULT_LIMIT);
	private int limit;
	private LinkedList<Integer> uniqueList;

	public static final synchronized RandomUnique global() {
		return GLOBAL;
	}

	public RandomUnique() {
		limit = 10;
		uniqueList = new LinkedList<>();
		for (int j = 1; j <= limit; ++j) uniqueList.addLast(j);
	}

	public RandomUnique(int limit) {
		this.limit = limit;
		uniqueList = new LinkedList<>();
		for (int j = 1; j <= limit; ++j) uniqueList.addLast(j);
	}

	public RandomUnique(int limit, LinkedList<Integer> uniqueList) {
		this.limit = limit;
		this.uniqueList = uniqueList;
	}

	public int generateDefault() {
		if (!uniqueList.isEmpty()) return uniqueList.removeFirst();
		for (int j = 1; j <= DEFAULT_LIMIT; ++j) uniqueList.addLast(j);
		Collections.shuffle(uniqueList);
		return uniqueList.removeFirst();
	}

	public int generate() {
		if (!uniqueList.isEmpty()) return uniqueList.removeFirst();
		for (int j = 1; j <= limit; ++j) uniqueList.addLast(j);
		Collections.shuffle(uniqueList);
		return uniqueList.removeFirst();
	}

	public int generate(boolean async) {
		return async ? generateAsyncAndGet() : generate();
	}

	public CompletableFuture<Integer> generateAsync() {
		return CompletableFuture.supplyAsync(() -> {
			if (!uniqueList.isEmpty()) return uniqueList.removeFirst();
			for (int j = 1; j <= limit; ++j) uniqueList.addLast(j);
			Collections.shuffle(uniqueList);
			return uniqueList.removeFirst();
		});
	}

	public int generateAsyncAndGet() {
		return generateAsync().join();
	}

	public int setRandomLimit(int limit) {
		return this.limit = limit;
	}

	public int getRandomLimit() {
		return limit;
	}
}
