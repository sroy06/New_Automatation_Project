package tcs

import capture.pages.Capture
import common.modules.ext.form.field.Text
import common.util.WaitUtil
import compose.api.ComposeClient
import compose.pages.Compose
import compose.pages.ComposeRecent
import compose.util.BioviaSignIn
import geb.spock.GebReportingSpec
import spock.lang.*
import java.awt.Robot
import java.awt.event.KeyEvent

import static compose.util.Setup.ADMINUSER
import static compose.util.Setup.ADMINUSER
import static compose.util.Setup.ADMPASSWORD
import static compose.util.Setup.ADMPASSWORD
import static compose.util.Setup.createRecipe
import static compose.util.Setup.getRecipeID
import hub.pages.Hub

/*
    This script is testing below functionalities
    1. Login to Compose
    2. Create a Control Recipe
    3. Add Amount & Date/Time parameter
    4. Recipe approval
    5. Recipe execution in Capture
 */

@Stepwise
class CisproMapping extends GebReportingSpec{

    @Shared ComposeClient client
    @Shared String recipeID
    @Shared materialsDetails = [
            [ Name: 'M1', Value:'1', Unit: 'g', Role: 'Input']
            ]

    void setupSpec() {
        given: 'Direct to Compose'
        via ComposeRecent

        when: 'Sign into ComposeRecent via BioviaSignIn page'
        waitFor { at BioviaSignIn }
        signIn(ADMINUSER, ADMPASSWORD)
        client = new ComposeClient(ADMINUSER, ADMPASSWORD, baseUrl)

        then: 'On the Compose Recent page'
        waitFor('slow') { at ComposeRecent }
    }

    void 'Create a recipe'() {
        when: 'Create a recipe'
        WaitUtil.ignoreCommonExceptionsAndRetry { createRecipe() }
        waitFor { at Compose }
        createRecipeWindow.handleRecipe(getBrowser(), ['Control Recipe_'+ new Date().toString(), 'Master', 'General', 'Add Method', '/Recipe Library/Amgen'], 'Create')

        then: 'The recipe is created'
        waitFor('slow') { !getBrowser().find('.x-masked') }
        waitFor('slow') { at Compose }
    }

  /*  void 'Material Addition' () {

        when: 'Adding Material details'
         recipeID = getRecipeID(getCurrentUrl())

         waitFor { navTab.lists('Materials') }.click()
         Thread.sleep(5000)

         waitFor { materialPanel.toolbar.buttons('Add') }.click()
        then: 'Create Material'
        addRecipeMaterials(materialsDetails, 0, true)

    }*/
    void 'Move to Process tab'() {
        when: 'Navigate to the Process section'
        recipeID = getRecipeID(getCurrentUrl())
        waitFor { navTab.lists('Process') }.click()

        then: 'Arrive at the Process section'
        waitFor('slow') { !getBrowser().find('.x-masked') }
        waitFor { at Compose }
        waitFor { processBrowserView }
    }

    @Unroll
      void 'Addition of Parameters' () {

          given: 'Still at Compose'
          waitFor { at Compose }

          when: 'Expand the Generic stage and add a parameter/annotation'
          processBrowserView.processTree.expand('New Process/Generic Stage', 'name').click()
          verifyProcessStepSelected('Generic Stage')

          processBrowserView.clickTab(param)
          waitFor { processBrowserView.findProcessParameters(index).toolbar.buttons('Add') }.click()
          waitFor('slow') { !getBrowser().find('.x-masked') }
          if (param == 'Parameters') {
              parameterSelectDialog(0).filterAndSelect(field, [[Name: field]], false)
          } else {
              annotationSelectDialog(0).selectAnnotations([field])
          }

          then: 'The dialog closes'
          waitFor('slow') { !getBrowser().find('.x-masked') }

          toolbar.buttons('Save').click()
         /* waitFor { !toolbar.buttons('Save').isEnabled() }
          driver.navigate().refresh()*/

        waitFor('slow') { !getBrowser().find('.x-masked') }
        processBrowserView.processTree.expand('New Process/Generic Stage', 'name').click()

        where: 'Using the following data'
        loop | param        | index | field
        0    | 'Parameters' | 0     | 'Density'
       // 1    | 'Parameters' | 0     | 'Concentration'

    }
/*
    void 'Test tagging for each of the parameters'() {
        when: 'click on the tag button for the parameter'
        waitFor ('slow') { processBrowserView.findTagButton('Density', 0) }.click()

        then: 'select Process Input'
        tagEditorDialog.comboBox.set('Process Input')
        tagEditorDialog.buttons('Apply').click()

        when: 'click on the tag button for the parameter'
        waitFor ('slow') { processBrowserView.findTagButton('Concentration', 0) }.click()

        then: 'select Process Input'
        tagEditorDialog.comboBox.set('Process Input')
        tagEditorDialog.buttons('Apply').click()

    }*/
    /*void 'Add the Material with Stage' () {

        when: 'Move to Materials tab'
        recipeID = getRecipeID(getCurrentUrl())
        waitFor {$("span", text: "Materials")}.click()

        and: 'Add the Material'
        waitFor {$("span", text: "Add")}.click()
        Robot robot = new Robot()
        robot.keyPress(KeyEvent.VK_TAB)
        robot.keyPress(KeyEvent.VK_TAB)
      //robot.keyPress(com.sun.glass.events.KeyEvent.VK_ENTER)
        getDriver().switchTo().activeElement().click()
       // waitFor {$("div.x-grid-row-checker", 1)}.click()
        waitFor {$("span", text: "Add Selected")}.click()
        toolbar.buttons('Save').click()
        waitFor { !toolbar.buttons('Save').isEnabled() }

       // waitFor {$ ("div.x-grid-checkcolumn", 1)}.click()

        getDriver().switchTo().activeElement().click()
        then: 'Arrive at the Process section'
        waitFor('slow') { !getBrowser().find('.x-masked') }
        waitFor { at Compose }

        and: 'Saving the Recipe'
        toolbar.buttons('Save').click()
        waitFor { !toolbar.buttons('Save').isEnabled() }
       // driver.navigate().refresh()

    }*/

    Void 'Mapping View' (){
        when: 'Move to Parameter tab'
       // recipeID = getRecipeID(getCurrentUrl())
      //  waitFor {$("span", text: "Parameters")}.click()
        waitFor {$("span", id: "button-1146-btnIconEl")}.click()
        waitFor {$("img", id: "tool-1313-toolEl")}.click()

         then:   wait(11000)

    }

  /*  void 'Submit the recipe'() {
        when: 'switch work flow state to Submit'
        waitFor('slow') { toolbar.splitButton('Current State: Draft') }.click()
        waitFor { menu.item('Submit') }.click()

        then: 'switch work flow state to in progress'
        waitFor { !mask.isDisplayed() && toolbar.splitButton('Current State: Submit').isEnabled() }
    }

    void 'Approve the recipe'() {
        when: 'switch work flow state to Approve'
        waitFor('slow') { toolbar.splitButton('Current State: Submit') }.click()
        waitFor { menu.item('Approve') }.click()

        then: 'switch work flow state to in progress'
        waitFor { !mask.isDisplayed() && toolbar.splitButton('Current State: Approved').isEnabled() }
    }



    void 'View in Capture'() {
        when: 'wait for view in capture to enable'
        waitFor('slow') { toolbar.buttons('View in Capture').isEnabled() }
        waitFor { toolbar.buttons('View in Capture') }.click()

        then: 'direct to capture page'
        waitFor { at Capture }
    }

    void 'Recipe execution in Capture'() {
        when: 'Providing Recipe Values'
        refresh()

        // Data Fill for Recipe execution
        recipeLayout.fillOutDates(1, 'Not Now') // Date/Time
        recipeLayout.formGroup(0).set('50') // Amount

        recipeLayout.clickSave() // Saving data
        recipeLayout.clickDone() // Clicking Done button

        // Recipe completion
        waitFor('slow') { recipeStatusBar.button('complete').click() }
        waitFor { applicationsPanel.yesButton }.click()

        then: 'Moved to HUB'
        Thread.sleep(20000)
        assert title == "BIOVIA Hub"
    }


    void cleanupSpec(){
        go ComposeRecent.logoutUrl
    }*/



}
