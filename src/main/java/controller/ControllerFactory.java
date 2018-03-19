package controller;

/**
 * ControllerFactory is a suite which is in charge of storing all controllers in charge of manipulating components found in the
 * PDA
 */
public class ControllerFactory {
    //all controllers responsible for controlling parts of the application are stored in this application
    // and can be accessed in a static context anywhere in the application
    public static PDARunnerController pdaRunnerController;
    public static HomeController homeController;
    public static LibraryController libraryController;
    public static ToolBarPartialController toolBarPartialController;
    public static QuickDefinitionController quickDefinitionController;
    public static ExamplesController examplesController;
    public static InfoController infoController;
    public static HelpController helpController;
}
