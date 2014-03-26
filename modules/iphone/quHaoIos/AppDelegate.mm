//
//  AppDelegate.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "AppDelegate.h"

@implementation AppDelegate
@synthesize window = _window;
@synthesize tabBarController = _tabBarController;
@synthesize homeVc;
@synthesize nearVc;
@synthesize mineVc;
@synthesize moreVc;
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    //s设置 UserAgent
    [ASIHTTPRequest setDefaultUserAgentString:@"QuhaoiPhone"];
    
    //系统托盘
    //[application setStatusBarHidden:NO withAnimation:UIStatusBarAnimationFade];
    //主页
    self.homeVc=[[HomeViewController alloc] init];
    BaseNavigationController *homeNavigation=[[BaseNavigationController alloc]initWithRootViewController:homeVc];
    homeNavigation.title=@"主页";

    //周边
    self.nearVc=[[NearViewController alloc] init];
    BaseNavigationController *nearNavigation=[[BaseNavigationController alloc]initWithRootViewController:nearVc];
        
    //我的
    self.mineVc=[[MineViewController alloc] init];
    BaseNavigationController *mineNavigation=[[BaseNavigationController alloc]initWithRootViewController:mineVc];

    //更多
    self.moreVc=[[SettingView alloc] init];
    BaseNavigationController *moreNavigation=[[BaseNavigationController alloc]initWithRootViewController:moreVc];
    moreNavigation.navigationBarHidden = NO;
    
    self.tabBarController = [[UITabBarController alloc] init];
    self.tabBarController.delegate = self;
    self.tabBarController.viewControllers = [NSArray arrayWithObjects:
                                             homeNavigation,
                                             nearNavigation,
                                             mineNavigation,
                                             moreNavigation,
                                             nil];
    //初始化
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.rootViewController = self.tabBarController;
    [self.window makeKeyAndVisible];
    
    if ([Helper returnUserString:@"showImage"]==nil)
    {
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults setObject:@"1" forKey:@"showImage"];
        [defaults synchronize];
    }
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    if (![Helper isConnectionAvailable]) {
        UIAlertView *myalert = [[UIAlertView alloc] initWithTitle:@"警告" message:@"未连接网络" delegate:self cancelButtonTitle:@"确认" otherButtonTitles:nil,nil];
		[myalert show];
    }

}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

#pragma mark UITab双击事件
- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController
{
    int newTabIndex = self.tabBarController.selectedIndex;
    if (newTabIndex == m_lastTabIndex) {
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Notification_TabClick" object:[NSString stringWithFormat:@"%d", newTabIndex]];
    }
    else
    {
        m_lastTabIndex = newTabIndex;
    }
}

@end
