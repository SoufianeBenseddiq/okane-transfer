package com.okanetransfer.service.dto.aml.response;

import java.util.List;

public class AmlDashboardResponse {

    private Long totalDeclarations;
    private Long declarationsNonTraitees;
    private Long totalRegles;
    private Long reglesActives;
    private Long entreeOFAC;
    private List<DeclarationResponse> recentDeclarations;
    private List<AuditResponse> recentAudits;

    public AmlDashboardResponse() {
    }

    public AmlDashboardResponse(Long totalDeclarations, Long declarationsNonTraitees,
                               Long totalRegles, Long reglesActives, Long entreeOFAC,
                               List<DeclarationResponse> recentDeclarations,
                               List<AuditResponse> recentAudits) {
        this.totalDeclarations = totalDeclarations;
        this.declarationsNonTraitees = declarationsNonTraitees;
        this.totalRegles = totalRegles;
        this.reglesActives = reglesActives;
        this.entreeOFAC = entreeOFAC;
        this.recentDeclarations = recentDeclarations;
        this.recentAudits = recentAudits;
    }

    public Long getTotalDeclarations() {
        return totalDeclarations;
    }

    public void setTotalDeclarations(Long totalDeclarations) {
        this.totalDeclarations = totalDeclarations;
    }

    public Long getDeclarationsNonTraitees() {
        return declarationsNonTraitees;
    }

    public void setDeclarationsNonTraitees(Long declarationsNonTraitees) {
        this.declarationsNonTraitees = declarationsNonTraitees;
    }

    public Long getTotalRegles() {
        return totalRegles;
    }

    public void setTotalRegles(Long totalRegles) {
        this.totalRegles = totalRegles;
    }

    public Long getReglesActives() {
        return reglesActives;
    }

    public void setReglesActives(Long reglesActives) {
        this.reglesActives = reglesActives;
    }

    public Long getEntreeOFAC() {
        return entreeOFAC;
    }

    public void setEntreeOFAC(Long entreeOFAC) {
        this.entreeOFAC = entreeOFAC;
    }

    public List<DeclarationResponse> getRecentDeclarations() {
        return recentDeclarations;
    }

    public void setRecentDeclarations(List<DeclarationResponse> recentDeclarations) {
        this.recentDeclarations = recentDeclarations;
    }

    public List<AuditResponse> getRecentAudits() {
        return recentAudits;
    }

    public void setRecentAudits(List<AuditResponse> recentAudits) {
        this.recentAudits = recentAudits;
    }
}
