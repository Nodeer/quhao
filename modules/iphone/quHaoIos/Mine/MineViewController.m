//
//  MineViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#define UserImage userOrigin.jpg
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
        NSString * autoLogin = [Helper returnUserString:@"autoLogin"];
        //如果已经登录
        if((autoLogin==nil||[autoLogin boolValue])&&[Helper isCookie])
        {
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                NSString *name = [Helper getUserName];
                NSString *pwd = [Helper getPwd];
                NSString *urlStr=[NSString stringWithFormat:@"%@%@",IP,@"/login"];
                ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:[NSURL URLWithString:urlStr]];
                [request setUseCookiePersistence:YES];
                [request setPostValue:name forKey:@"phone"];
                [request setPostValue:pwd  forKey:@"password"];
                [request setPostValue:autoLogin forKey:@"keep_login"];
                [request setDelegate:self];
                [request setDidFailSelector:@selector(requestFailed:)];
                [request setDidFinishSelector:@selector(requestLogin:)];
                [request startAsynchronous];
                
            });
        }

    }
    return self;
}
- (void)viewDidLoad
{
    [super viewDidLoad];
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
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.navigationController.navigationBar.translucent = NO;
    self.tabBarController.tabBar.translucent = NO;
    self.extendedLayoutIncludesOpaqueBars = NO;
#endif
    _helper = [Helper new];
    //设置tableView不能滚动
    [_mineView setScrollEnabled:NO];
    if(kDeviceHeight >480 ){
        [self setExtraCellLineHidden:_mineView];
    }
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(noticeUpdateHandler:) name:@"Notification_NoticeUpdate" object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshed:) name:Notification_TabClick object:nil];
}

- (void)viewDidAppear:(BOOL)animated
{
    //登录判断
    if (_userInfo==nil) {
        _userInfo= [UserInfo alloc];
    }
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self loadByLoginType];

        dispatch_async(dispatch_get_main_queue(), ^{
            [_mineView reloadData];

        });
    });

}

- (void)loadByLoginType
{
  if(self.isLoginJustNow)
    {
        self.isLoginJustNow = NO;
        _helper.viewBeforeLogin = nil;
        _helper.viewNameBeforeLogin = nil;
        [self reload];
    }
    //如果cookie存在且不是刚刚登录的话
    else if(!self.isLoginJustNow&&[Helper isCookie]==YES){
        self.isLoginJustNow = NO;
        _helper.viewBeforeLogin = nil;
        _helper.viewNameBeforeLogin = nil;
        [self reload];
        
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
    [Helper saveCookie:NO];
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
            [Helper saveCookie:YES];
        }
            break;
        default:
        {
            [Helper saveCookie:NO];
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
            self.egoImgView = [[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]];
            //self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
            self.egoImgView.frame = CGRectMake(10, 10, 110, 110);
            [cell.contentView addSubview:self.egoImgView];
            if ([[Helper returnUserString:@"showImage"] boolValue])
            {
                if ([Helper isCookie]&&[Helper isFileExist:@"userOrigin.jpg"]) {
                    self.egoImgView.image = [Helper imageWithImageSimple:[UIImage imageWithContentsOfFile:[self userImagePath]] scaledToSize:CGSizeMake(100, 100)];
                }else{
                    if(nil==_userInfo.imgUrl||[_userInfo.imgUrl isEqualToString:@""])
                    {
                        self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
                    }else{
                        self.egoImgView.imageURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,_userInfo.imgUrl]];
                    }
                }
            }
            
            self.egoImgView.userInteractionEnabled = YES;
            UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(uploadPortrait:)];
            [self.egoImgView addGestureRecognizer:singleTap];
        
            
            UILabel *_numberLabel = [Helper getCustomLabel:@"您还没登录,马上登录" font:18 rect:CGRectMake(egoImgView.frame.origin.x+egoImgView.frame.size.width+15,20, 220, 35)];
            _numberLabel.font = [UIFont systemFontOfSize:18];
            [cell.contentView addSubview:_numberLabel];
            if ([Helper isCookie] == NO || _userInfo == nil){
                _numberLabel.text=@"您还没登录,马上登录";
            }else{
                _numberLabel.text = _userInfo.username;
            }
            UILabel *_jfLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@ %d",@"剩余积分 ",_userInfo.jifen] font:18 rect:CGRectMake(_numberLabel.frame.origin.x, _numberLabel.frame.origin.y+_numberLabel.frame.size.height+3, 190, 35)];
            _jfLabel.font = [UIFont systemFontOfSize:18];
            [cell.contentView addSubview:_jfLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] ==1 ) { //签到和点评
            UILabel *_qdLabel = [Helper getCustomLabel:@"签到" font:18 rect:CGRectMake(kDeviceWidth/3-10, 12, 60, 30)];
            if(_userInfo.isSignIn){
                _qdLabel.textColor=[UIColor blackColor];
            }else{
                _qdLabel.textColor=[UIColor redColor];
            }
            UITapGestureRecognizer *qdGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickUILable:)];
            _qdLabel.userInteractionEnabled=YES;
            _qdLabel.font = [UIFont systemFontOfSize:18];
            [_qdLabel addGestureRecognizer:qdGesture];
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
            
            UILabel *_dpValueLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%d",_userInfo.dianping] font:18 rect:CGRectMake(_dpLabel.frame.origin.x+10, _dpLabel.frame.size.height+8, 60, 30)];
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

- (void)setExtraCellLineHidden: (UITableView *)tableView
{
    UIView *view =[ [UIView alloc]init];
    view.backgroundColor = [UIColor whiteColor];
    [tableView setTableFooterView:view];
}

//设置cell的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([Helper isCookie] == NO) {
        UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:@"请登录后查看信息" delegate:self cancelButtonTitle:@"返回" destructiveButtonTitle:nil otherButtonTitles:@"登录", nil];
        [sheet showInView:[UIApplication sharedApplication].keyWindow];
        
        return;
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    if (row ==0) {
        [self pushMineInfo];
    }else if (row ==3) {
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
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    UILabel *la=(UILabel *)tap.view;
    if ([Helper isCookie] == NO) {
        UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:@"请登录后查看信息" delegate:self cancelButtonTitle:@"返回" destructiveButtonTitle:nil otherButtonTitles:@"登录", nil];
        [sheet showInView:[UIApplication sharedApplication].keyWindow];
        return;
    }
    if(_userInfo.isSignIn){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"今日已签过，明天再来签到吧" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    la.userInteractionEnabled = NO;
    if([Helper isConnectionAvailable]){
        NSString *urlStr=[NSString stringWithFormat:@"%@%@?accountId=%@",IP,signIn_url,_userInfo.accountId];
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
                        _userInfo.isSignIn = YES;
                        _userInfo.signIn=[[jsonObjects valueForKey:@"signIn"] intValue];
                        _userInfo.jifen=[[jsonObjects valueForKey:@"jifen"] intValue];
                        NSIndexPath *te=[NSIndexPath indexPathForRow:1 inSection:0];
                        [_mineView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:te,nil] withRowAnimation:UITableViewRowAnimationFade];
                    }else{
                        [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
                    }
                }
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
    la.userInteractionEnabled = YES;
}

//点击点评
-(void)onClickDp:(UITapGestureRecognizer *)sender{
    if ([Helper isCookie] == NO) {
        UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:@"请登录后查看信息" delegate:self cancelButtonTitle:@"返回" destructiveButtonTitle:nil otherButtonTitles:@"登录", nil];
        [sheet showInView:[UIApplication sharedApplication].keyWindow];
        
        return;
    }
    if(_userInfo.dianping ==0 ){
        return;
    }
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

- (void)pushMineInfo
{
    MineInfoViewController *info = [[MineInfoViewController alloc] init];
    info.name =_userInfo.username;
    info.jifen = _userInfo.jifen;
    info.accountId = _userInfo.accountId;
    info.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:info animated:YES];
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
    NSString *urlStr=[NSString stringWithFormat:@"%@%@%@",IP,person_url,[Helper getUserName]];
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
    }else if([buttonTitle isEqualToString:@"拍照"]){
        [self snapImage];
    }else if([buttonTitle isEqualToString:@"从相册上传"]){
        [self pickImageFromAlbum];
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

#pragma mark - upload image
//上传图片操作开始，选择图片的来源
-(void)uploadPortrait:(id)sender{
    if ([Helper isCookie] == NO) {
        return;
    }
    UIActionSheet *menu = [[UIActionSheet alloc]
                           initWithTitle: @"更改图片"
                           delegate:self
                           cancelButtonTitle:@"取消"
                           destructiveButtonTitle:nil
                           otherButtonTitles:@"拍照",@"从相册上传",nil];
    menu.actionSheetStyle =UIActionSheetStyleBlackTranslucent;
    [menu showInView:self.navigationController.view];
}

- (void)pickImageFromAlbum
{
    UIImagePickerController* imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    imagePicker.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    imagePicker.allowsEditing = YES;
    [self presentViewController:imagePicker animated:YES completion:nil];
    
}

- (void)snapImage
{
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        UIImagePickerController* imagePicker = [[UIImagePickerController alloc] init];
        imagePicker.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
        imagePicker.delegate = self;
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        imagePicker.allowsEditing = YES;
        [self presentViewController:imagePicker animated:YES completion:nil];
    }else{
        NSLog(@"模拟器无法打开相机");
    }
}

- (void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    UIImage *image= [info objectForKey:@"UIImagePickerControllerEditedImage"];
    if (picker.sourceType == UIImagePickerControllerSourceTypeCamera)
    {
        UIImageWriteToSavedPhotosAlbum(image, self,
                               @selector(image:didFinishSavingWithError:contextInfo:),
                                nil);
    }else{
        [self saveImageData:image];
    }
}
-(void)saveImageData:(UIImage *)image
{
    //原生图片
    NSData *imageData = UIImageJPEGRepresentation(image,1);
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* documentsDirectory = [paths objectAtIndex:0];
    NSString* fullPathToFile = [documentsDirectory stringByAppendingPathComponent:@"userOrigin.jpg"];
    [imageData writeToFile:fullPathToFile atomically:NO];
    
    [self upLoadSalesBigImage:imageData];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo;
{
    if (error)
    {
        [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
    }else{
        [self saveImageData:image];
    }
}

- (void)upLoadSalesBigImage:(NSData *)bigImage
{
    NSString *url=[NSString stringWithFormat:@"%@%@",IP,upload_pic];
    ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:[NSURL URLWithString:url]];
    [request setRequestMethod:@"POST"];
    [request setPostValue:_userInfo.accountId forKey:@"accountId"];
    [request setData:bigImage forKey:@"userImage"];
    [request setDelegate:self];
    //[request setTimeOutSeconds:TIME_OUT_SECONDS];
    [request setDidFailSelector:@selector(uploadFailed:)];
    [request setDidFinishSelector:@selector(uploadSuccess:)];
    [request startSynchronous];
}

- (void)uploadSuccess:(ASIHTTPRequest *)requestNew
{
    NSString *responseString = [requestNew responseString];
    if([responseString isEqualToString:@"success"]){
        NSIndexPath *te=[NSIndexPath indexPathForRow:0 inSection:0];
        [_mineView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:te,nil] withRowAnimation:UITableViewRowAnimationMiddle];
    }else{
        [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
    }
}

- (void)uploadFailed:(ASIHTTPRequest *)requestNew
{
    [Helper showHUD2:@"服务器错误，请稍后再试" andView:self.view andSize:130];
}

- (NSString *)userImagePath
{
    return [NSHomeDirectory() stringByAppendingPathComponent:@"Documents/userOrigin.jpg"];
}
#pragma mark - View lifecycle
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
}
@end
