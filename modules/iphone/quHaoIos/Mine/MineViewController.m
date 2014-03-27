//
//  MineViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "MineViewController.h"

@interface MineViewController ()

@end

@implementation MineViewController
@synthesize egoImgView;

- (id)init
{
    self = [super init];
    if (self) {
        self.title=@"我的";
        self.tabBarItem.image = [UIImage imageNamed:@"mine"];
        _helper=[Helper new];
        NSString * autoLogin = [Helper returnUserString:@"autoLogin"];
        //如果已经登录
        if((autoLogin==nil||[autoLogin boolValue])&&_helper.isCookie==YES)
        {
            NSString *name = [Helper getUserName];
            NSString *pwd = [Helper getPwd];
            NSString *urlStr=[NSString stringWithFormat:@"%@%@",[Helper getIp],@"/login"];
            ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:[NSURL URLWithString:urlStr]];
            [request setUseCookiePersistence:YES];
            [request setPostValue:name forKey:@"phone"];
            [request setPostValue:pwd  forKey:@"password"];
            [request setPostValue:autoLogin forKey:@"keep_login"];
            [request setDelegate:self];
            [request setDidFailSelector:@selector(requestFailed:)];
            [request setDidFinishSelector:@selector(requestLogin:)];
            [request startAsynchronous];
        }

    }
    return self;
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    //UIView  *view=[[UIView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];
    //view.backgroundColor=[UIColor whiteColor];
    //self.view=view;
    _mineView=[[UITableView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight) style:UITableViewStylePlain];
    _mineView.dataSource=self;
    _mineView.delegate=self;
    _mineView.backgroundColor=[UIColor whiteColor];
    _mineView.indicatorStyle=UIScrollViewIndicatorStyleWhite;
    [self.view addSubview:_mineView];
#if IOS7_SDK_AVAILABLE
    if ([_mineView respondsToSelector:@selector(setSeparatorInset:)]) {
        [_mineView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(noticeUpdateHandler:) name:@"Notification_NoticeUpdate" object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshed:) name:Notification_TabClick object:nil];
}

- (void)viewDidAppear:(BOOL)animated
{
    //登录判断
    if (_userInfo==nil) {
        _userInfo= [UserInfo alloc];
    }
    [self loadByLoginType];
}

- (void)loadByLoginType
{
    if (_helper.isCookie == NO) {
        UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:@"请登录后查看信息" delegate:self cancelButtonTitle:@"返回" destructiveButtonTitle:nil otherButtonTitles:@"登录", nil];
        [sheet showInView:[UIApplication sharedApplication].keyWindow];
    }
    //如果已经登录 则判断是否是刚刚登录  如果是  则刷新要不要？
    else if(self.isLoginJustNow)
    {
        self.isLoginJustNow = NO;
        _helper.viewBeforeLogin = nil;
        _helper.viewNameBeforeLogin = nil;
        [self reload];
        [_mineView reloadData];
    }
    //如果cookie存在且不是刚刚登录的话
    else if(!self.isLoginJustNow&&_helper.isCookie==YES){
        self.isLoginJustNow = NO;
        _helper.viewBeforeLogin = nil;
        _helper.viewNameBeforeLogin = nil;
        [self reload];
        
        [_mineView reloadData];
    }
}
- (void)refreshed:(NSNotification *)notification
{
    if (notification.object) {
        if ([(NSString *)notification.object isEqualToString:@"2"]) {
            [self loadByLoginType];
        }
    }
}

- (void)requestFailed:(ASIHTTPRequest *)requestNew
{
    [_helper saveCookie:NO];
}

- (void)requestLogin:(ASIHTTPRequest *)requestNew
{
    [Helper getUserNotice:requestNew];
    [requestNew setUseCookiePersistence:YES];
    ApiError *error = [Helper getApiError:requestNew];
    
    if (error == nil) {
        return;
    }
    switch (error.errorCode) {
        case 0:
        {
            [_helper saveCookie:YES];
        }
            break;
        default:
        {
            [_helper saveCookie:NO];
        }
            break;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    int row = [indexPath row];
    if(row==0){
        return 130;
    }else if(row==1){
        return 76;
    }
    return 53;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CellTableIdentifier"];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"CellTabeIndentifier"];
        if ([indexPath row] ==0 ) {//用户行
            self.egoImgView = [[EGOImageView alloc] initWithFrame:CGRectMake(10, 10, 110, 110)];
            self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
            [cell.contentView addSubview:self.egoImgView];
            if (![[Helper returnUserString:@"showImage"] boolValue]||nil==_userInfo.imgUrl||[_userInfo.imgUrl isEqualToString:@""])
            {
                self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
            }
            else
            {
                self.egoImgView.imageURL = [NSURL URLWithString:_userInfo.imgUrl];
            }
            
            UILabel *_numberLabel = [Helper getCustomLabel:@"" font:18 rect:CGRectMake(egoImgView.frame.origin.x+egoImgView.frame.size.width+15,20, 220, 35)];
            Helper *helper=[Helper new];
            if (helper.isCookie == NO){
                _numberLabel.text=@"您还没有登录哦";
            }else{
                _numberLabel.text=[NSString stringWithFormat:@"%@%@",@"qh",_userInfo.phone];
            }
            _numberLabel.font = [UIFont systemFontOfSize:18];
            [cell.contentView addSubview:_numberLabel];
            
            UILabel *_jfLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@ %d",@"剩余积分 ",_userInfo.jifen] font:18 rect:CGRectMake(_numberLabel.frame.origin.x, _numberLabel.frame.origin.y+_numberLabel.frame.size.height+3, 190, 35)];
            _jfLabel.font = [UIFont systemFontOfSize:18];
            [cell.contentView addSubview:_jfLabel];
            
        }else if ([indexPath row] ==1 ) { //签到和点评           
            UILabel *_qdLabel = [Helper getCustomLabel:@"签到" font:18 rect:CGRectMake(90, 12, 60, 30)];
            if(_userInfo.isSignIn){
                _qdLabel.textColor=[UIColor blackColor];
            }else{
                _qdLabel.textColor=[UIColor redColor];
                UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickUILable:)];
                _qdLabel.userInteractionEnabled=YES;
                _qdLabel.font = [UIFont systemFontOfSize:18];
                [_qdLabel addGestureRecognizer:tapGesture];
            }
            [cell.contentView addSubview:_qdLabel];
            
            UILabel *_qdValueLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%d",_userInfo.signIn] font:18 rect:CGRectMake(_qdLabel.frame.origin.x+10, _qdLabel.frame.size.height+8, 60, 30)];
            _qdValueLabel.font = [UIFont systemFontOfSize:18];
            [cell.contentView addSubview:_qdValueLabel];
            
            UILabel *_dpLabel = [Helper getCustomLabel:@"点评" font:18 rect:CGRectMake(_qdLabel.frame.origin.x+_qdLabel.frame.size.width+35, _qdLabel.frame.origin.y, _qdLabel.frame.size.width, 30)];
            UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickDp:)];
            _dpLabel.userInteractionEnabled=YES;
            _dpLabel.font = [UIFont systemFontOfSize:18];
            [_dpLabel addGestureRecognizer:tapGesture];
            [cell.contentView addSubview:_dpLabel];
            
            UILabel *_dpValueLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%d",_userInfo.dianping] font:18 rect:CGRectMake(_dpLabel.frame.origin.x+18, _dpLabel.frame.size.height+8, 60, 30)];
            UITapGestureRecognizer *tapGesture2=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickDp:)];
            _dpValueLabel.userInteractionEnabled=YES;
            [_dpValueLabel addGestureRecognizer:tapGesture2];
            _dpValueLabel.font = [UIFont systemFontOfSize:18];
            [cell.contentView addSubview:_dpValueLabel];
            
        }else if ([indexPath row] == 2) {
            
            cell.textLabel.text = @"当前取号情况";
            cell.imageView.image = [UIImage imageNamed:@"mine_dqqh"];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            [Helper arrowStyle:cell];

        }else if ([indexPath row] == 3) {
            
            cell.textLabel.text = @"历史取号情况";
            cell.imageView.image = [UIImage imageNamed:@"mine_lsqh"];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            [Helper arrowStyle:cell];

        }else if ([indexPath row] == 4) {
            
            cell.textLabel.text = @"积分消费情况";
            cell.imageView.image = [UIImage imageNamed:@"mine_jfsf"];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            [Helper arrowStyle:cell];

        }
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    }
    return cell;
    
}

//设置cell的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    if (row ==3) {
        [self pushHistoryMerchart];
    }else if(row==2){
        [self pushCurrentMerchart];
    }else if(row==4){
        [self pushCreditView];
    }
}

//点击签到的
-(void)onClickUILable:(UITapGestureRecognizer *)sender
{        
    if([Helper isConnectionAvailable]){
        NSString *urlStr=[NSString stringWithFormat:@"%@%@?accountId=%@",[Helper getIp],signIn_url,_userInfo.accountId];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
        }else{
            NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
            }else{
                if(jsonObjects.count!=0){
                    int errorCode=[[jsonObjects valueForKey:@"errorCode"] intValue];
                    if (errorCode==1) {
                        [Helper showHUD2:@"签到成功" andView:self.view andSize:100];
                        [self reload];
                        [_mineView reloadData];
                        
                    }else{
                        [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
                    }
                }
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//点击点评
-(void)onClickDp:(UITapGestureRecognizer *)sender{
    CommentViewController *history = [[CommentViewController alloc] init];
    history.accountOrMerchantId=_userInfo.accountId;
    history.title = @"我的评论";
    history.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:history animated:YES];
}

//点击历史取号
- (void)pushHistoryMerchart
{
    MerchartHistoryController *history = [[MerchartHistoryController alloc] init];
    history.accouId=_userInfo.accountId;
    history.title = @"历史取号情况";
    history.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:history animated:YES];
    
}

//点击当前取号
- (void)pushCurrentMerchart
{
    CurrentViewController *current = [[CurrentViewController alloc] init];
    current.accouId=_userInfo.accountId;
    current.title = @"当前取号情况";
    current.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:current animated:YES];
    
}

//点击消费情况
- (void)pushCreditView
{
    CreditViewController *credit = [[CreditViewController alloc] init];
    credit.accouId=_userInfo.accountId;
    credit.title = @"积分消费情况";
    credit.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:credit animated:YES];
    
}


-(void)reload
{
    NSString *urlStr=[NSString stringWithFormat:@"%@%@%@",[Helper getIp],person_url,[Helper getUserName]];
    NSString *response =[QuHaoUtil requestDb:urlStr];
    
    if([response isEqualToString:@""]){
        //异常处理
        [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
        //UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"数据错误，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        //[alert show];
    }else{
        NSArray *jsonObjects=[QuHaoUtil analyseData:response];
        if(jsonObjects==nil){
            //解析错误
            [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
        }else{
            _userInfo.username=[jsonObjects valueForKey:@"nickname"];
            _userInfo.jifen=[[jsonObjects valueForKey:@"jifen"] intValue];
            _userInfo.signIn=[[jsonObjects valueForKey:@"signIn"] intValue];
            _userInfo.dianping=[[jsonObjects valueForKey:@"dianping"] intValue];
            _userInfo.phone=[jsonObjects valueForKey:@"phone"];
            _userInfo.accountId=[jsonObjects valueForKey:@"accountId"];
            _userInfo.userImage=[jsonObjects valueForKey:@"userImage"];
            _userInfo.isSignIn=[[jsonObjects valueForKey:@"isSignIn"] boolValue];
        }
    }
}

//登录事件
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSString *buttonTitle = [actionSheet buttonTitleAtIndex:buttonIndex];
    if ([buttonTitle isEqualToString:@"登录"]) {
        LoginView *loginView = [[LoginView alloc] init];
        loginView._isPopupByNotice = YES;
        _helper.viewBeforeLogin = self;
        _helper.viewNameBeforeLogin = @"MineViewController";
        loginView.helper=_helper;
        loginView.hidesBottomBarWhenPushed=YES;
        [self.navigationController pushViewController:loginView animated:YES];
        return;
    }
}

//登录监听
- (void)noticeUpdateHandler:(NSNotification *)notification
{
         UserInfo * temp = (UserInfo *)[notification object];
         _userInfo.phone=temp.phone;
         _userInfo.username=temp.username;
         _userInfo.signIn=temp.signIn;
         _userInfo.dianping=temp.dianping;
         _userInfo.accountId=temp.accountId;
         _userInfo.userImage=temp.userImage;
         _userInfo.isSignIn=temp.isSignIn;
}
#pragma mark - View lifecycle
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
@end
