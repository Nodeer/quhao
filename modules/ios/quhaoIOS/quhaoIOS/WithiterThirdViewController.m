//
//  WithiterThirdViewController.m
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import "WithiterThirdViewController.h"

@interface WithiterThirdViewController ()

@end

@implementation WithiterThirdViewController

@synthesize mobileLabel;
@synthesize passwordLabel;
@synthesize mobile;
@synthesize password;
@synthesize signupBTN;
@synthesize loginBTN;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"个人中心", @"个人中心");
        self.tabBarItem.image = [UIImage imageNamed:@"first"];
    }
    return self;
}

/*
 * Sign up click function
 */
-(IBAction)onClickSignUpBTN:(id)sender{
    UIAlertView *alert =
    [[UIAlertView alloc] initWithTitle:@"Alert" message:@"注册!" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

/**
 * Login click function
 */
-(IBAction)onClickLoginBTN:(id)sender{
    UIAlertView *alert =
    [[UIAlertView alloc] initWithTitle:@"Alert" message:@"登陆!" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    NSLog(@"aaaaa");
    
    [loginBTN addTarget:self action:@selector(onClickLoginBTN:) forControlEvents:UIControlEventTouchUpInside];
    [signupBTN addTarget:self action:@selector(onClickSignUpBTN:) forControlEvents:UIControlEventTouchUpInside];
    
    
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
