package org.egov.council.entity;

public class MeetingMoMRestDataView {
	 private Long id;

	    
	    private String councilMeeting ;

	   
	    private String councilAgenda ;

	    
	    private String councilPreamble ;

	  
	    private String resolutionDetail;

	   
	    private String  resolutionStatus;

	  
	    private String resolutionNumber;
	    
	    private boolean isLegacy;
	    
	    private String filename;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getCouncilMeeting() {
			return councilMeeting;
		}

		public void setCouncilMeeting(String councilMeeting) {
			this.councilMeeting = councilMeeting;
		}

		public String getCouncilAgenda() {
			return councilAgenda;
		}

		public void setCouncilAgenda(String councilAgenda) {
			this.councilAgenda = councilAgenda;
		}

		public String getCouncilPreamble() {
			return councilPreamble;
		}

		public void setCouncilPreamble(String councilPreamble) {
			this.councilPreamble = councilPreamble;
		}

		public String getResolutionDetail() {
			return resolutionDetail;
		}

		public void setResolutionDetail(String resolutionDetail) {
			this.resolutionDetail = resolutionDetail;
		}

		public String getResolutionStatus() {
			return resolutionStatus;
		}

		public void setResolutionStatus(String resolutionStatus) {
			this.resolutionStatus = resolutionStatus;
		}

		public String getResolutionNumber() {
			return resolutionNumber;
		}

		public void setResolutionNumber(String resolutionNumber) {
			this.resolutionNumber = resolutionNumber;
		}

		public boolean isLegacy() {
			return isLegacy;
		}

		public void setLegacy(boolean isLegacy) {
			this.isLegacy = isLegacy;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
	    
	    

}
