package com.codefundo.votenew;

public class displayresult {
    String party;
    String votes;
    String partyimg;
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public displayresult(String party, String votes, String partyimg, String time) {
        this.party = party;
        this.votes = votes;
        this.partyimg = partyimg;
        this.time = time;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public displayresult() {
    }

    public String getPartyimg() {
        return partyimg;
    }

    public void setPartyimg(String partyimg) {
        this.partyimg = partyimg;
    }
}
