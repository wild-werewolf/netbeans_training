/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensode.nbbook.model;

import java.io.Serializable;

/**
 *
 * @author WereWolf
 */
public class SurveyData implements Serializable{
    private String fullName;
    private String[] progLangList;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     *
     * @return
     */
    public String[] getProgLangList() {
        return progLangList;
    }

    public void setProgLangList(String[] progLangList) {
        this.progLangList = progLangList;
    }
}
