/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.git;

import hudson.model.FreeStyleProject;
import hudson.plugins.git.util.DefaultBuildChooser;
import hudson.triggers.SCMTrigger;

import org.kohsuke.stapler.HttpResponse;

import java.util.Collections;
import org.jvnet.hudson.test.HudsonTestCase;
import org.mockito.Mockito;

public class GitStatusTest extends HudsonTestCase {
    private GitStatus gitStatus;

    public GitStatusTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.gitStatus = new GitStatus();
    }

    public void testGetDisplayName() {
        assertEquals("Git", this.gitStatus.getDisplayName());
    }

    public void testGetSearchUrl() {
        assertEquals("git", this.gitStatus.getSearchUrl());
    }

    public void testGetIconFileName() {
        assertNull(this.gitStatus.getIconFileName());
   }

    public void testGetUrlName() {
        assertEquals("git", this.gitStatus.getUrlName());
    }

    public void testDoNotifyCommitWithNoBranches() throws Exception {
        SCMTrigger aMasterTrigger = setupProject("a", "master");
        SCMTrigger aTopicTrigger = setupProject("a", "topic");
        SCMTrigger bMasterTrigger = setupProject("b", "master");
        SCMTrigger bTopicTrigger = setupProject("b", "topic");

        this.gitStatus.doNotifyCommit("a", "");
        Mockito.verify(aMasterTrigger).run();
        Mockito.verify(aTopicTrigger).run();
        Mockito.verify(bMasterTrigger, Mockito.never()).run();
        Mockito.verify(bTopicTrigger, Mockito.never()).run();
    }

    public void testDoNotifyCommitWithNoMatchingUrl() throws Exception {
        SCMTrigger aMasterTrigger = setupProject("a", "master");
        SCMTrigger aTopicTrigger = setupProject("a", "topic");
        SCMTrigger bMasterTrigger = setupProject("b", "master");
        SCMTrigger bTopicTrigger = setupProject("b", "topic");

        this.gitStatus.doNotifyCommit("nonexistent", "");
        Mockito.verify(aMasterTrigger, Mockito.never()).run();
        Mockito.verify(aTopicTrigger, Mockito.never()).run();
        Mockito.verify(bMasterTrigger, Mockito.never()).run();
        Mockito.verify(bTopicTrigger, Mockito.never()).run();
    }

    public void testDoNotifyCommitWithOneBranch() throws Exception {
        SCMTrigger aMasterTrigger = setupProject("a", "master");
        SCMTrigger aTopicTrigger = setupProject("a", "topic");
        SCMTrigger bMasterTrigger = setupProject("b", "master");
        SCMTrigger bTopicTrigger = setupProject("b", "topic");

        this.gitStatus.doNotifyCommit("a", "master");
        Mockito.verify(aMasterTrigger).run();
        Mockito.verify(aTopicTrigger, Mockito.never()).run();
        Mockito.verify(bMasterTrigger, Mockito.never()).run();
        Mockito.verify(bTopicTrigger, Mockito.never()).run();
    }

    public void testDoNotifyCommitWithTwoBranches() throws Exception {
        SCMTrigger aMasterTrigger = setupProject("a", "master");
        SCMTrigger aTopicTrigger = setupProject("a", "topic");
        SCMTrigger bMasterTrigger = setupProject("b", "master");
        SCMTrigger bTopicTrigger = setupProject("b", "topic");

        this.gitStatus.doNotifyCommit("a", "master,topic");
        Mockito.verify(aMasterTrigger).run();
        Mockito.verify(aTopicTrigger).run();
        Mockito.verify(bMasterTrigger, Mockito.never()).run();
        Mockito.verify(bTopicTrigger, Mockito.never()).run();
    }

    public void testDoNotifyCommitWithNoMatchingBranches() throws Exception {
        SCMTrigger aMasterTrigger = setupProject("a", "master");
        SCMTrigger aTopicTrigger = setupProject("a", "topic");
        SCMTrigger bMasterTrigger = setupProject("b", "master");
        SCMTrigger bTopicTrigger = setupProject("b", "topic");

        this.gitStatus.doNotifyCommit("a", "nonexistent");
        Mockito.verify(aMasterTrigger, Mockito.never()).run();
        Mockito.verify(aTopicTrigger, Mockito.never()).run();
        Mockito.verify(bMasterTrigger, Mockito.never()).run();
        Mockito.verify(bTopicTrigger, Mockito.never()).run();
    }

    private SCMTrigger setupProject(String url, String branchString) throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        project.setScm(new GitSCM(
                null,
                Collections.singletonList(new UserRemoteConfig(url, null, null)),
                Collections.singletonList(new BranchSpec(branchString)),
                null,
                false, Collections.<SubmoduleConfig>emptyList(), false,
                false, new DefaultBuildChooser(), null, null, false, null,
                null,
                null, null, null, false, false, false, false, null, null, false, null));
        SCMTrigger trigger = Mockito.mock(SCMTrigger.class);
        project.addTrigger(trigger);
        return trigger;
    }
}
