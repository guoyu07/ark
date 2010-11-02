/**
 * 
 * This is a new file
 *
 *
 */
package au.org.theark.study.web.component.subject;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import au.org.theark.core.vo.SubjectVO;
import au.org.theark.study.service.IStudyService;
import au.org.theark.study.web.Constants;
import au.org.theark.study.web.component.subject.form.ContainerForm;

/**
 * @author nivedann
 *
 */
public class SubjectContainer extends Panel{

	private ContainerForm containerForm;
	private WebMarkupContainer detailsContainer;
	private WebMarkupContainer searchWebMarkupContainer;
	private WebMarkupContainer resultListContainer;//Search Results Container
	private IModel<Object> iModel;
	private PageableListView<SubjectVO> pageableListView;
	private FeedbackPanel feedBackPanel;
	private Details detailsPanel;
	
	private SearchResults searchResultsPanel;
	
	private Search searchPanel;
	@SpringBean( name = Constants.STUDY_SERVICE)
	private IStudyService studyService;
	
	private void initialiseMarkupContainers(){
		/* The markup container for search panel */
		searchWebMarkupContainer = new WebMarkupContainer("searchContainer");
		searchWebMarkupContainer.setOutputMarkupPlaceholderTag(true);
		
		detailsContainer = new WebMarkupContainer("detailsContainer");
		detailsContainer.setOutputMarkupPlaceholderTag(true);
		detailsContainer.setVisible(false);
		
		resultListContainer = new WebMarkupContainer("resultListContainer");
		resultListContainer.setOutputMarkupPlaceholderTag(true);
		resultListContainer.setVisible(true);
	}
	
	private WebMarkupContainer initialiseSearchPanel(){
		
		Long sessionStudyId = (Long)SecurityUtils.getSubject().getSession().getAttribute(au.org.theark.core.Constants.STUDY_CONTEXT_ID);
		Collection<SubjectVO> list = new ArrayList<SubjectVO>();
		
		if(sessionStudyId != null && sessionStudyId > 0){
			
			containerForm.getModelObject().setStudy(studyService.getStudy(sessionStudyId));
			list = studyService.getSubject(containerForm.getModelObject());	
		}
		containerForm.getModelObject().setSubjectList(list);
		
		searchPanel = new Search(	"searchPanel", 
									feedBackPanel, 
									searchWebMarkupContainer, 
									pageableListView, 
									resultListContainer, 
									detailsContainer, 
									detailsPanel,
									containerForm);
		
		searchPanel.initialisePanel();
		searchWebMarkupContainer.add(searchPanel);
		return searchWebMarkupContainer;
	}
	
	private FeedbackPanel initialiseFeedBackPanel(){
		/* Feedback Panel */
		feedBackPanel= new FeedbackPanel("feedbackMessage");
		feedBackPanel.setOutputMarkupId(true);
		return feedBackPanel;
	}
	
	private WebMarkupContainer initialiseDetailPanel(){
		detailsPanel = new Details("detailsPanel", resultListContainer, feedBackPanel, detailsContainer, searchWebMarkupContainer, containerForm);
		detailsPanel.initialisePanel();
		detailsContainer.add(detailsPanel);
		return detailsContainer;
		
	}
	
	
	private WebMarkupContainer initialiseSearchResults(){
		
		searchResultsPanel = new SearchResults("resultsPanel", detailsContainer, searchWebMarkupContainer, containerForm, resultListContainer, detailsPanel);
		
		iModel = new LoadableDetachableModel<Object>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Object load() {
				return containerForm.getModelObject().getSubjectList();
			}
		};

		pageableListView  = searchResultsPanel.buildPageableListView(iModel);
		pageableListView.setReuseItems(true);
		PagingNavigator pageNavigator = new PagingNavigator("navigator", pageableListView);
		searchResultsPanel.add(pageNavigator);
		searchResultsPanel.add(pageableListView);
		resultListContainer.add(searchResultsPanel);
		return resultListContainer;
	}
	
	/**
	 * @param id
	 */
	public SubjectContainer(String id) {
		
		super(id);

		initialiseMarkupContainers();
		containerForm = new ContainerForm("containerForm", new CompoundPropertyModel<SubjectVO>(new SubjectVO()));
		
		containerForm.add(initialiseFeedBackPanel());
		containerForm.add(initialiseDetailPanel());
		containerForm.add(initialiseSearchResults());
		containerForm.add(initialiseSearchPanel());
		
		add(containerForm);
		
	}

}
