//
//  YouhuiViewController.m
//  quHaoIos
//
//  Created by sam on 14-5-12.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "YouhuiViewController.h"

@interface YouhuiViewController ()

@end

@implementation YouhuiViewController
@synthesize mid;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"优惠详情";
    
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    self.view.backgroundColor  = [ UIColor whiteColor];
    _titles = [[NSMutableArray alloc] init];
    _contexts = [[NSMutableArray alloc] init];
    [self createHud];
    [self requestData];
    [self labelFactory];
    [_HUD hide:YES];
}

- (void)labelFactory
{
    CGRect sectionFrame;
    CGFloat width = self.view.frame.size.width;
    CGFloat xOffset = 10;
    CGFloat height = 20.0;
    
    for (int i=0; i < _titles.count; i++) {
        id item = [_titles objectAtIndex:i];
        
        sectionFrame = CGRectMake(xOffset, height, width - xOffset, 0.0);
        UILabel *textLabel = [[UILabel alloc] initWithFrame:sectionFrame];
        textLabel.backgroundColor = [UIColor clearColor];
        textLabel.numberOfLines = 0;
        textLabel.textColor = [UIColor darkGrayColor];
        textLabel.font = [UIFont boldSystemFontOfSize:14.0];
        textLabel.text = [NSString stringWithFormat:@"%d.%@", i+1 , item ];
        [textLabel sizeToFit];
        height += textLabel.bounds.size.height + 10;
        [self.view addSubview:textLabel];
        
        
        sectionFrame = CGRectMake(xOffset+10, height, width - xOffset-15, 0.0);
        UILabel *contextLabel = [[UILabel alloc] initWithFrame:sectionFrame];
        contextLabel.backgroundColor = [UIColor clearColor];
        contextLabel.numberOfLines = 0;
        contextLabel.textColor = [UIColor darkGrayColor];
        contextLabel.font = [UIFont fontWithName:@"Arial" size:13.0];
        contextLabel.text = [_contexts objectAtIndex:i];
        [contextLabel sizeToFit];
        
        //CGFloat sectionSpace = i < self.items.count - 1 ? self.itemsSpacing : 0.0;
        height += contextLabel.bounds.size.height + 10;
        //sectionFrame = CGRectMake(xOffset, height, width - xOffset, 0.0);
        [self.view addSubview:contextLabel];
    }
}

-(void)requestData
{
    if ([Helper isConnectionAvailable])
    {
        NSString *response =[QuHaoUtil requestDb:[NSString stringWithFormat:@"%@%@?mid=%@",IP,getYouHui_url,mid]];
        if([response isEqualToString:@"false"]){
            //异常处理
            _HUD.labelText = @"服务器错误";
        }else{
            NSArray *list = [QuHaoUtil analyseData:response];
            for(int i=0; i<[list count]; i++){
                [_titles addObject:[[list objectAtIndex:i] objectForKey:@"title"]];
                [_contexts addObject:[[list objectAtIndex:i] objectForKey:@"content"]];
            }
        }
    }else
    {
        _HUD.labelText = @"当前网络不可用";
    }
}

#pragma mark HUD
- (void)hudWasHidden:(MBProgressHUD *)hud {
    [_HUD removeFromSuperview];
	_HUD = nil;
}

-(void)createHud
{
    _HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:_HUD];
    _HUD.mode = MBProgressHUDModeIndeterminate;
    _HUD.labelText = @"正在加载";
    [_HUD show:YES];
    _HUD.delegate = self;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
