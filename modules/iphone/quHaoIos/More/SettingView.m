//
//  SettingView.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "SettingView.h"


@implementation SettingView
@synthesize tableSettings;
@synthesize settings;
@synthesize settingsInSection;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = @"更多";
        self.tabBarItem.title = @"更多";
        self.tabBarItem.image = [UIImage imageNamed:@"more"];
    }
    return self;
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.settingsInSection = [[NSMutableDictionary alloc] initWithCapacity:3];
    //BOOL isLogin = [Helper Instance].isCookie;
    NSArray *first = [[NSArray alloc] initWithObjects:
                      [[SettingModel alloc] initWith:@"图片设置" andImg:@"image" andTag:1 andTitle2:nil],
                      [[SettingModel alloc] initWith:@"注销" andImg:@"out" andTag:8 andTitle2:nil],
                      [[SettingModel alloc] initWith:@"清理缓存" andImg:@"clear" andTag:2 andTitle2:nil],
                      nil];

    NSArray *second = [[NSArray alloc] initWithObjects:
                      [[SettingModel alloc] initWith:@"分享微博" andImg:@"share" andTag:3 andTitle2:nil],
                      [[SettingModel alloc] initWith:@"意见反馈" andImg:@"feedback" andTag:4 andTitle2:nil],
                      [[SettingModel alloc] initWith:@"关于我们" andImg:@"about" andTag:5 andTitle2:nil],
                      [[SettingModel alloc] initWith:@"检测更新" andImg:@"setting" andTag:6 andTitle2:nil],
                      [[SettingModel alloc] initWith:@"帮助" andImg:@"help" andTag:7 andTitle2:nil],
                      nil];
    [self.settingsInSection setObject:first forKey:@"设置"];
    [self.settingsInSection setObject:second forKey:@"关于"];
    self.settings = [[NSArray alloc] initWithObjects:@"设置",@"关于",nil];
    
    CGSize size=CGSizeMake(500,44);
    [self.navigationController.navigationBar setBackgroundImage:[Helper reSizeImage:@"title.jpg" toSize:size] forBarMetrics:UIBarMetricsDefault];
}

- (void)viewDidAppear:(BOOL)animated
{
    if([[NSUserDefaults standardUserDefaults] objectForKey:@"showImage"]==nil){
        _showImage=1;
    }else{
        _showImage=[[Helper returnUserString:@"showImage"] boolValue];
    }
    [self refresh];
}

- (void)viewDidUnload
{
    [self setTableSettings:nil];
    [super viewDidUnload];
}

- (void)refresh
{
    [self.tableSettings reloadData];
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
        //图片设置
        case 1:
        {
            return;
        }
            break;
        //清理缓存
        case 2:
        {               
            [self dirCache];
        }
            break;
        //分享
        case 3:
        {
            [self sharedApp:self.navigationController];
        }
            break;
        //意见反馈
        case 4:
        {
            FeedbackViewController *feedback = [FeedbackViewController new];
            feedback.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:feedback animated:YES];
        }
            break;
        //关于我们
        case 5:
        {
            About *about = [About new];
            about.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:about animated:YES];
        }
            break;
        //检测更新
        case 6:
        {
           [self checkVersionNeedUpdate];
        }
            break;
        //帮助
        case 7:
        {
            About *about = [About new];
            about.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:about animated:YES];
        }
            break;
        //注销
        case 8:
        {
            Helper *helper=[Helper new];
            if (helper.isCookie == NO) {
                [Helper ToastNotification:@"错误 您还没有登录,注销无效" andView:self.view andLoading:NO andIsBottom:NO];
                return;
            }
            
            [ASIHTTPRequest setSessionCookies:nil];
            [ASIHTTPRequest clearSession];
            helper.isLogin = NO;
            [helper saveCookie:NO];
            
            [self refresh];
            
            [[NSNotificationCenter defaultCenter] postNotificationName:@"login" object:@"0"];
            [Helper ToastNotification:@"注销成功" andView:self.view andLoading:NO andIsBottom:NO];
        }
            break;
        default:
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 50;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [settings count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSString *key = [settings objectAtIndex:section];
    NSArray *set = [settingsInSection objectForKey:key];
    return [set count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = [indexPath section];
    NSUInteger row = [indexPath row];
    NSString *key = [settings objectAtIndex:section];
    NSArray *sets = [settingsInSection objectForKey:key];
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SettingTableIdentifier"];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"SettingTableIdentifier"];
    }
    //设置图片按钮
    if(indexPath.section==0&&indexPath.row==0){
        UISwitch *Switch=[[UISwitch alloc] initWithFrame:CGRectMake(220, 11, 79, 27)];
        [Switch addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
        Switch.tag=101;
        Switch.on=_showImage;
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];

        [cell.contentView addSubview:Switch];
    }else if(indexPath.section==0&&(indexPath.row==1 || indexPath.row==2)){
        [Helper arrowStyle:cell];
    }
    
    SettingModel *model = [sets objectAtIndex:row];
    cell.textLabel.text = model.title;
    cell.imageView.image = [UIImage imageNamed:model.img];
    cell.tag = model.tag;
    return cell;
}

-(void)switchAction:(UISwitch *)sender
{
    NSInteger switchTag=sender.tag;
    BOOL switchStatus=sender.on;//Switch的状态
    NSNumber *convertSwitchStatus=[[NSNumber alloc] initWithBool:switchStatus];
    switch (switchTag) {
        case 101:
            [[NSUserDefaults standardUserDefaults] setObject:convertSwitchStatus forKey:@"showImage"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            break;
        default:
            break;
    }
}

//检测更新
- (void)checkVersionNeedUpdate
{
    if ([Helper isConnectionAvailable]){
        NSString *str1=[NSString stringWithFormat:@"%@%@",[Helper getIp],getLastestVersion];
        NSString *response =[QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                NSString * version=[jsonObjects objectForKey:@"ios"];
                if ([SettingView getVersionNumber:version]>[SettingView getVersionNumber:AppVersion]) {
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"温馨提示" message:@"客户端有新版了\n您需要下载吗?" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
                    [alert setTag: 1];

                    [alert show];
                }else
                {
                    UIAlertView * alert = [[UIAlertView alloc] initWithTitle:@"温馨提示" message:@"您当前已经是最新版本" delegate:self cancelButtonTitle:@"返回" otherButtonTitles:nil, nil];
                    [alert show];
                }
            }
        }    
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//比较版本
+ (int)getVersionNumber:(NSString *)version
{
    NSArray * arr = [version componentsSeparatedByString:@"."];
    if (arr.count >= 3) {
        NSString * a = [arr objectAtIndex:0];
        NSString * b = [arr objectAtIndex:1];
        NSString * c = [arr objectAtIndex:2];
        return a.intValue * 100 + b.intValue * 10 + c.intValue;
    }
    else{
        return 0;
    }
}

//下载
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([alertView tag]==1 && buttonIndex == 1) {
        //根据应用的id打开appstore，并跳转到应用下载页面
        //NSString *appStoreLink = [NSString stringWithFormat:@"http://itunes.apple.com/cn/app/id%@",AppID];
        //[[UIApplication sharedApplication] openURL:[NSURL URLWithString:appStoreLink]];
        
        NSString *appStoreLink = [NSString stringWithFormat:@"itms-apps://phobos.apple.com/WebObjects/MZStore.woa/wa/viewSoftware?id=%@",AppID];
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:appStoreLink]];
    }else if ([alertView tag]==2 && buttonIndex == 1) {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"access_token" ];
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"wb_uid" ];
    }

}

//清除缓存
-(void)dirCache{
    [[EGOCache currentCache] clearCache];

    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"access_token" ];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"wb_uid" ];
    
    NSArray *cacPath = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *path = [cacPath lastObject];
    NSFileManager *manager = [NSFileManager defaultManager];
    NSDictionary *attributes = [manager attributesOfItemAtPath:path error:nil];
    if ([manager isDeletableFileAtPath:path]) {
        [manager removeItemAtPath:path error:nil];
        [manager createDirectoryAtPath:path withIntermediateDirectories:YES attributes:attributes error:nil];
        [self.tableSettings reloadData];

        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:NSLocalizedString(@"提示", @"prompt") message:NSLocalizedString(@"清除成功!", @"clear cache already") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles: nil];
        [alert show];
    }
}

-(void)sharedApp:(UINavigationController *)navController{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"access_token"] != nil) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"温馨提示" message:@"已绑定新浪微博\n您需要解除吗?" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
        [alert setTag: 2];
        [alert show];
    }else{
        OAuthWebViewController *auth = [[OAuthWebViewController alloc] init];
        auth.hidesBottomBarWhenPushed=YES;
        [navController pushViewController:auth animated:YES];
    }
}
@end
