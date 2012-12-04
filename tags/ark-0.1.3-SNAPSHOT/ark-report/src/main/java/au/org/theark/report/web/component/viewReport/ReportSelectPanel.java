package au.org.theark.report.web.component.viewReport;

import java.util.List;

import mx4j.log.Log;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.theark.core.dao.ArkAuthorisationDao;
import au.org.theark.core.exception.EntityNotFoundException;
import au.org.theark.core.model.report.entity.ReportTemplate;
import au.org.theark.core.model.study.entity.ArkUser;
import au.org.theark.core.model.study.entity.Study;
import au.org.theark.core.service.IArkCommonService;
import au.org.theark.report.model.vo.ReportSelectVO;
import au.org.theark.report.service.Constants;
import au.org.theark.report.service.IReportService;
import au.org.theark.report.web.component.viewReport.consentDetails.ConsentDetailsReportContainer;
import au.org.theark.report.web.component.viewReport.phenoFieldDetails.PhenoFieldDetailsReportContainer;
import au.org.theark.report.web.component.viewReport.studyLevelConsent.StudyLevelConsentReportContainer;
import au.org.theark.report.web.component.viewReport.studySummary.StudySummaryReportContainer;

@SuppressWarnings("serial")
public class ReportSelectPanel extends Panel
{
	private static Logger log = LoggerFactory.getLogger(ReportSelectPanel.class);

	@SpringBean(name = au.org.theark.report.service.Constants.REPORT_SERVICE)
	private IReportService reportService;
	
	@SpringBean( name = au.org.theark.core.Constants.ARK_COMMON_SERVICE)
	private IArkCommonService iArkCommonService;
	
	protected IModel<Object> iModel;
	CompoundPropertyModel<ReportSelectVO> reportSelectCPM;

	private ReportContainerVO		reportContainerVO;

	private PageableListView<ReportTemplate> pageableListView;

	public ReportSelectPanel(String id, CompoundPropertyModel<ReportSelectVO> reportSelectCPM, ReportContainerVO reportContainerVO)
	{
		super(id);
		this.reportContainerVO = reportContainerVO;
		this.reportSelectCPM = reportSelectCPM;
		reportContainerVO.setReportSelectPanel(this);
	}

	public void initialisePanel()
	{
		Subject subject = SecurityUtils.getSubject();
		Long sessionStudyId = (Long)subject.getSession().getAttribute(au.org.theark.core.Constants.STUDY_CONTEXT_ID);
		Study study = null;

		if(sessionStudyId != null && sessionStudyId > 0) {			
			study = iArkCommonService.getStudy(sessionStudyId);
			reportSelectCPM.getObject().setStudy(study);
		}
		ArkUser arkUser;
		try { 
			arkUser = iArkCommonService.getArkUser(subject.getPrincipal().toString());
//			List<ReportTemplate> resultList = reportService.getReportsAvailableList(arkUser, study);
			List<ReportTemplate> resultList = reportService.getReportsAvailableList(null, null);
			
			if (resultList == null || (resultList != null && resultList.size() == 0)) {
				this.info("No reports are available to you under your current role (NB: roles may depend on the study in context)");
			}
			reportSelectCPM.getObject().setReportsAvailableList(resultList);
		} catch (EntityNotFoundException e) {
			log.error("ReportSelectPanel.initialisePanel() could not load the ArkUser based on username in context.  This should not happen.");
			this.error("A system error has occured. Please notify support if this happens after trying again.");
		}
		
		iModel = new LoadableDetachableModel<Object>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Object load() {
				pageableListView.removeAll();
				return reportSelectCPM.getObject().getReportsAvailableList();
			}
		};

		pageableListView = buildPageableListView(iModel);
		pageableListView.setReuseItems(true);
		PagingNavigator pageNavigator = new PagingNavigator("navigator", pageableListView);
		add(pageNavigator);
		add(pageableListView);
	}
	
	/**
	 * 
	 * @param iModel
	 * @param searchContainer
	 * @return
	 */
	public PageableListView<ReportTemplate> buildPageableListView(IModel iModel){
		
		PageableListView<ReportTemplate> sitePageableListView = new PageableListView<ReportTemplate>("reportList", iModel, au.org.theark.core.Constants.ROWS_PER_PAGE) {
			@Override
			protected void populateItem(final ListItem<ReportTemplate> item) {
				
				ReportTemplate reportTemplate = item.getModelObject();
				
				/* The report module */
				// TODO : will need to change to foreign key reference when new ARK security is implemented
				if(reportTemplate.getModule() != null){
					//Add the study Component Key here
					item.add(new Label("reportTemplate.module.name", reportTemplate.getModule().getName()));	
				}else{
					item.add(new Label("reportTemplate.module.name",""));
				}
				
				// Perform security check upon selection of the report
				Subject subject = SecurityUtils.getSubject();
				boolean securityCheckOk = false;
				try {
					String userRole = iArkCommonService.getUserRole(subject.getPrincipal().toString(), reportTemplate.getFunction(), 
							reportTemplate.getModule(), reportSelectCPM.getObject().getStudy());
					if (userRole.length() > 0) {
						securityCheckOk = true;
					}
				} catch (EntityNotFoundException e) {
					// TODO I don't like this kind of code - if there isn't a record, we should just return NULL.
					// Only if it really is an error to not have a record, then we should throw an exception.
				}
				item.setVisible(securityCheckOk);
				
				/* Component Name Link */
				item.add(buildLink(reportTemplate));
				
				//TODO when displaying text escape any special characters
				/* Description */
				if(reportTemplate.getDescription() != null){
					item.add(new Label("reportTemplate.description", reportTemplate.getDescription()).setEscapeModelStrings(false));//the ID here must match the ones in mark-up	
				}else{
					item.add(new Label("reportTemplate.description", ""));//the ID here must match the ones in mark-up
				}
			
				/* For the alternative stripes */
				item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel() {
					@Override
					public String getObject() {
						return (item.getIndex() % 2 == 1) ? "even" : "odd";
					}
				}));
				
			}
		};
		return sitePageableListView;
	}
	
	
	@SuppressWarnings({ "unchecked", "serial" })
	private AjaxLink buildLink(final ReportTemplate reportTemplate) {
		
		AjaxLink link = new AjaxLink("reportTemplate.link") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				// Perform security check upon selection of the report
				Subject subject = SecurityUtils.getSubject();
				boolean securityCheckOk = false;
				try {
					String userRole = iArkCommonService.getUserRole(subject.getPrincipal().toString(), reportTemplate.getFunction(), 
							reportTemplate.getModule(), reportSelectCPM.getObject().getStudy());
					if (userRole.length() > 0) {
						securityCheckOk = true;
					}
				} catch (EntityNotFoundException e) {
					// TODO I don't like this kind of code - if there isn't a record, we should just return NULL.
					// Only if it really is an error to not have a record, then we should throw an exception.
				}
				
				if (securityCheckOk == false) {
					this.error("You do not have enough privileges to access this report.  If you believe this is incorrect, then please contact your administrator.");
				}
				else if (reportTemplate.getName().equals(Constants.STUDY_SUMMARY_REPORT_NAME)) {
					if (reportSelectCPM.getObject().getStudy() == null) {
						this.error("This report requires a study in context. Please put a study in context first.");
					} 
					else {
						StudySummaryReportContainer selectedReportPanel = new StudySummaryReportContainer("selectedReportContainerPanel");
						selectedReportPanel.setOutputMarkupId(true);
						// Replace the old selectedReportPanel with this new one
						reportContainerVO.getSelectedReportPanel().replaceWith(selectedReportPanel);
						reportContainerVO.setSelectedReportPanel(selectedReportPanel);
						selectedReportPanel.initialisePanel(reportContainerVO.getFeedbackPanel(), reportTemplate);
						target.addComponent(reportContainerVO.getSelectedReportContainerWMC());
						this.info(reportTemplate.getName() + " template selected.");
					}
				} 
				else if (reportTemplate.getName().equals(Constants.STUDY_LEVEL_CONSENT_REPORT_NAME)) {
					if (reportSelectCPM.getObject().getStudy() == null) {
						this.error("This report requires a study in context. Please put a study in context first.");
					}
					else {
						StudyLevelConsentReportContainer selectedReportPanel = new StudyLevelConsentReportContainer("selectedReportContainerPanel");
						selectedReportPanel.setOutputMarkupId(true);
						// Replace the old selectedReportPanel with this new one
						reportContainerVO.getSelectedReportPanel().replaceWith(selectedReportPanel);
						reportContainerVO.setSelectedReportPanel(selectedReportPanel);
						selectedReportPanel.initialisePanel(reportContainerVO.getFeedbackPanel(), reportTemplate);
						target.addComponent(reportContainerVO.getSelectedReportContainerWMC());
						this.info(reportTemplate.getName() + " template selected.");
					}
				} 
				else if (reportTemplate.getName().equals(Constants.STUDY_COMP_CONSENT_REPORT_NAME)) {
					if (reportSelectCPM.getObject().getStudy() == null) {
						this.error("This report requires a study in context. Please put a study in context first.");
					}
					else {
						ConsentDetailsReportContainer selectedReportPanel = new ConsentDetailsReportContainer("selectedReportContainerPanel");
						selectedReportPanel.setOutputMarkupId(true);
						// Replace the old selectedReportPanel with this new one
						reportContainerVO.getSelectedReportPanel().replaceWith(selectedReportPanel);
						reportContainerVO.setSelectedReportPanel(selectedReportPanel);
						selectedReportPanel.initialisePanel(reportContainerVO.getFeedbackPanel(), reportTemplate);
						target.addComponent(reportContainerVO.getSelectedReportContainerWMC());
						this.info(reportTemplate.getName() + " template selected.");
					}
				}
				else if (reportTemplate.getName().equals(Constants.PHENO_FIELD_DETAILS_REPORT_NAME)) {
					if (reportSelectCPM.getObject().getStudy() == null) {
						this.error("This report requires a study in context. Please put a study in context first.");
					}
					else {
						PhenoFieldDetailsReportContainer selectedReportPanel = new PhenoFieldDetailsReportContainer("selectedReportContainerPanel");
						selectedReportPanel.setOutputMarkupId(true);
						// Replace the old selectedReportPanel with this new one
						reportContainerVO.getSelectedReportPanel().replaceWith(selectedReportPanel);
						reportContainerVO.setSelectedReportPanel(selectedReportPanel);
						selectedReportPanel.initialisePanel(reportContainerVO.getFeedbackPanel(), reportTemplate);
						target.addComponent(reportContainerVO.getSelectedReportContainerWMC());
						this.info(reportTemplate.getName() + " template selected.");
					}
				}
				else {
					this.error("System error: " + reportTemplate.getName() + " has no implementation or has been deprecated.");
				}
				target.addComponent(reportContainerVO.getFeedbackPanel());
			}
		};
		
		//Add the label for the link
		Label nameLinkLabel = new Label("reportTemplate.name", reportTemplate.getName());
		link.add(nameLinkLabel);
		return link;

	}

}