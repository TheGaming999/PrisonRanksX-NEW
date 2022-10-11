package me.prisonranksx.lists;

import java.util.List;

import me.prisonranksx.PrisonRanksX;

public class RanksTextList {

	private PrisonRanksX plugin;
	private String rankCurrentFormat;
	private String rankCompletedFormat;
	private String rankOtherFormat;
	private boolean enablePages;
	private int rankPerPage;
	private List<String> rankWithPagesListFormat;
	private List<String> rankListFormat;
	boolean isCustomList;
	private List<String> rankListFormatHeader;
	private List<String> rankListFormatFooter;
	private List<String> header;
	private List<String> footer;
	private List<String> ranksCollection;
	private List<String> currentRanks;
	private List<String> completedRanks;
	private List<String> otherRanks;
	private List<String> nonPagedRanks;
	private String lastPageReached;

	public RanksTextList(PrisonRanksX plugin) {
		this.plugin = plugin;
	}

	public void setup() {

	}

}
