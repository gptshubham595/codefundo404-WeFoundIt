package com.codefundo.votenew;

public class politicalparty {
    String party;
    String name;
    String image;
    String partyimg;
    int votes;

    public politicalparty() {
    }

    public politicalparty(String party, String name, String image, String partyimg, int votes) {
        this.party = party;
        this.name = name;
        this.image = image;
        this.partyimg = partyimg;
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPartyimg() {
        return partyimg;
    }

    public void setPartyimg(String partyimg) {
        this.partyimg = partyimg;
    }
}
