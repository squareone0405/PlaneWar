package gui;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import config.Config;

public class RankPanel extends JPanel {

	public RankPanel(ArrayList<Integer> scores) {
		super();
		this.setSize(Config.PANEL_WIDTH, Config.FRAME_HEIGHT);
		this.setOpaque(false);
		this.setLayout(null);

		JLabel rankLbl = new JLabel("×î¸ß·Ö");
		rankLbl.setBounds(Config.RANK_LABEL_X, Config.RANK_LABEL_Y, Config.RANK_LABEL_WIDTH, Config.RANK_LABEL_HEIGHT);
		rankLbl.setHorizontalAlignment(JLabel.CENTER);
		rankLbl.setOpaque(true);
		this.add(rankLbl);
		JLabel[] scoreLbl = new JLabel[Config.HIGH_SCORE_AMOUNT];
		for(int i = 0; i < Config.HIGH_SCORE_AMOUNT; ++i) {
			scoreLbl[i] = new JLabel(String.valueOf(scores.get(i).intValue()));
			scoreLbl[i].setBounds(Config.RANK_X, Config.RANK_Y + Config.RANK_GAP * i, Config.RANK_WIDTH, Config.RANK_HEIGHT);
			scoreLbl[i].setHorizontalAlignment(JLabel.CENTER);
			scoreLbl[i].setOpaque(true);
			this.add(scoreLbl[i]);
		}
		
		this.setVisible(true);
	}
}
