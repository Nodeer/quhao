//
//  MineInfoViewController.m
//  quHaoIos
//
//  Created by sam on 14-4-15.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "MineInfoViewController.h"

@interface MineInfoViewController ()

@end

@implementation MineInfoViewController
@synthesize settings;
@synthesize settingsInSection;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = @"账户信息";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    self.settingsInSection = [[NSMutableDictionary alloc] initWithCapacity:2];
    NSArray *first = [[NSArray alloc] initWithObjects:
                      [[SettingModel alloc] initWith:[NSString stringWithFormat:@"%@%d",@"现有积分:",_jifen] andImg:@"viewJiFen" andTag:1 andTitle2:nil],
                      [[SettingModel alloc] initWith:@"积分说明" andImg:@"jfsm" andTag:2 andTitle2:nil],
                      nil];
    
    NSArray *second = [[NSArray alloc] initWithObjects:
                       [[SettingModel alloc] initWith:_name andImg:@"logout_status" andTag:5 andTitle2:nil],
                       [[SettingModel alloc] initWith:@"修改账户密码" andImg:@"xgmm" andTag:6 andTitle2:nil],
                       nil];
    [self.settingsInSection setObject:first forKey:@"积分"];
    [self.settingsInSection setObject:second forKey:@"账户"];
    self.settings = [[NSArray alloc] initWithObjects:@"积分",@"账户",nil];
    
    self.tview = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight) style:UITableViewStyleGrouped];
    [self.tview setDelegate:self];
    [self.tview setDataSource:self];
    self.view.backgroundColor = [UIColor clearColor];
    self.tview.backgroundColor = [ UIColor colorWithRed: 0.947
                                                  green: 0.947
                                                   blue: 0.947
                                                  alpha: 1.0
                                  ];
    [self.view addSubview:self.tview];
#if IOS7_SDK_AVAILABLE
    if ([self.tview respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tview setSeparatorInset:UIEdgeInsetsZero];
    }
#else
    self.tview.backgroundView = nil;
#endif
    UIView* footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 360, 30.0)];
    UIImage *btnImage = [UIImage   imageNamed:@"max_btn.png"];
    UIButton *loginOutBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    loginOutBtn.frame = CGRectMake(10, 10, 300, 30);
    [loginOutBtn setBackgroundImage:btnImage forState:UIControlStateNormal];
    [loginOutBtn setTitle: @"退   出" forState: UIControlStateNormal];
    [loginOutBtn addTarget:self action:@selector(loginOut:) forControlEvents:UIControlEventTouchUpInside];
    [footerView addSubview:loginOutBtn];
    self.tview.tableFooterView = footerView;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(void)loginOut:(id)sender
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"温馨提示" message:@"确认要退出吗?" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
    [alert setTag: 1];
    [alert show];
}

//下载
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([alertView tag] == 1 && buttonIndex == 1) {
        [ASIHTTPRequest setSessionCookies:nil];
        [ASIHTTPRequest clearSession];
        [Helper saveCookie:NO];
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 44;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    NSString *key = [settings objectAtIndex:section];
    NSArray *set = [settingsInSection objectForKey:key];
    
    return [set count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return [settings count];;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    NSUInteger section = [indexPath section];
    NSUInteger row = [indexPath row];
    NSString *key = [settings objectAtIndex:section];
    NSArray *sets = [settingsInSection objectForKey:key];
    NSString *CellIdentifier = [NSString stringWithFormat:@"info%ld%ld",(long)section,(long)row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
        [Helper arrowStyle:cell];
    }
    SettingModel *model = [sets objectAtIndex:row];
    cell.textLabel.text = model.title;
    cell.textLabel.font = [UIFont systemFontOfSize:16];
    cell.imageView.image = [UIImage imageNamed:model.img];
    cell.tag = model.tag;
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSUInteger section = [indexPath section];
    NSString *key = [settings objectAtIndex:section];
    NSArray *sets = [settingsInSection objectForKey:key];
    SettingModel *action = [sets objectAtIndex:[indexPath row]];
    //开始处理
    switch (action.tag) {
            //积分查询
        case 1:
        {
            CreditViewController *credit = [[CreditViewController alloc] init];
            credit.accouId=self.accountId;
            credit.title = @"积分消费情况";
            credit.hidesBottomBarWhenPushed=YES;
            [self.navigationController pushViewController:credit animated:YES];
            
            return;
        }
            break;
            //积分说明
        case 2:
        {
            JFViewController *jf = [[JFViewController alloc] init];
            jf.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:jf animated:YES];
        }
            break;
            //修改用户名
        case 5:
        {
            UpdateNameViewController *updateName = [[UpdateNameViewController alloc] init];
            updateName.name = self.name;
            updateName.aid = self.accountId;
            updateName.delegate = self;
            updateName.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:updateName animated:YES];
        }
            break;
            //修改密码
        case 6:
        {
            UpdatePassViewController *update = [[UpdatePassViewController alloc] init];
            update.hidesBottomBarWhenPushed = YES;
            update.aid = self.accountId;
            [self.navigationController pushViewController:update animated:YES];
        }
            break;
        default:
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    
    return 15;
}

- (void)nameUpdate:(NSString*)username
{
    SettingModel *sm = [[settingsInSection objectForKey:@"账户"] objectAtIndex:0];
    sm.title = username;
    self.name = username;
    NSIndexPath *te=[NSIndexPath indexPathForRow:0 inSection:1];
    [self.tview reloadRowsAtIndexPaths:[NSArray arrayWithObjects:te,nil] withRowAnimation:UITableViewRowAnimationFade];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
