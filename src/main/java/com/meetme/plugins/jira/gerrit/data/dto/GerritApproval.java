/*
 * Copyright 2012 MeetMe, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.meetme.plugins.jira.gerrit.data.dto;

import com.sonymobile.tools.gerrit.gerritevents.dto.attr.Approval;
import net.sf.json.JSONObject;
import com.atlassian.jira.user.ApplicationUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sonymobile.tools.gerrit.gerritevents.dto.GerritEventKeys.BY;
import static com.sonymobile.tools.gerrit.gerritevents.dto.GerritEventKeys.EMAIL;
import static com.sonymobile.tools.gerrit.gerritevents.dto.GerritEventKeys.NAME;

public class GerritApproval extends Approval implements Comparable<GerritApproval> {
    private static final Logger log = LoggerFactory.getLogger(GerritApproval.class);


    /** The JIRA user associated with the same email */
    private ApplicationUser user;

    public GerritApproval() {
        super();
    }

    /**
     * Creates the PatchSetApproval from a {@link JSONObject}.
     *
     * @param json the JSON object with corresponding data.
     */
    public GerritApproval(JSONObject json) {
        super(json);
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public ApplicationUser getUser() {
        return this.user;
    }

    @Override
    public void fromJson(JSONObject json) {
        log.debug("GerritApproval from json: " + json.toString(4, 0));
        super.fromJson(json);
    }

    @Override
    public String getType() {
        return getUpgradedLabelType(super.getType());
    }

    public static String getUpgradedLabelType(String type) {
        // XXX: Older Gerrit versions had compact abbreviations for approval types;
        // translate those old abbreviations to the new expected types

        if ("CRVW".equals(type)) {
            return "Code-Review";
        } else if ("VRIF".equals(type)) {
            return "Verified";
        }

        return type;
    }

    /**
     * Returns the approval score as an integer.
     *
     * @return the integer approval score
     */
    public int getValueAsInt() {
        String value = getValue();

        if (value != null) {
            return Integer.parseInt(getValue(), 10);
        }

        return 0;
    }

    @Override
    public int compareTo(GerritApproval o) {
        int lhs = getValueAsInt();
        int rhs = o.getValueAsInt();

        if (lhs == rhs) {
            return 0;
        }

        return lhs > rhs ? 1 : -1;
    }

    @Override
    public String toString() {
        int value = getValueAsInt();
        return (value > 0 ? "+" : "") + value + " by " + getBy().getName();
    }
}
