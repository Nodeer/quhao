//
//  MerchartDetail.m
//  quHaoApp
//
//  Created by sam on 13-7-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import "MerchartDetail.h"
@interface MerchartDetail ()
@property (nonatomic, strong) MAMapView *mapView;

@end
@implementation MerchartDetail

@synthesize single;
@synthesize merchartID;
@synthesize isNextPage;
@synthesize mapView = _mapView;
@synthesize reservation;
@synthesize accountID;
@synthesize egoImgView;

-(void)loadNavigationItem
{
    self.title = NSLocalizedString(@"商家信息", @"商家信息");
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *qhBtn=[Helper getBackBtn:@"button.png" title:@"取 号" rect:CGRectMake( 0, 0, 40, 25 )];
    [qhBtn addTarget:self action:@selector(clickQuhao:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *btnItem = [[UIBarButtonItem alloc] initWithCustomView:qhBtn];
    self.navigationItem.rightBarButtonItem = btnItem;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    _detailView = [[UITableView alloc] initWithFrame:CGRectMake(0, 64, kDeviceWidth, kDeviceHeight) style:UITableViewStylePlain];
    _detailView.backgroundColor = [UIColor whiteColor];
    _detailView.indicatorStyle = UIScrollViewIndicatorStyleWhite;
    _detailView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.view = _detailView;
    self.view.backgroundColor = [UIColor whiteColor];
    self.single = [[MerchartModel alloc] init];
    _paiduArray = [[NSMutableArray alloc] initWithCapacity:20];
    accountID = @"";
    _isFirst = NO;
    NSString *isLogined = @"false";
    if ([Helper isCookie]){
        accountID =[Helper getUID];
        isLogined = @"true";
    }
    [self loadNavigationItem];
    
    if([Helper isConnectionAvailable]){
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = NSLocalizedString(@"正在加载", nil);
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            NSString *url = [NSString stringWithFormat:@"%@%@?merchantId=%@&accountId=%@&isLogined=%@",IP,merchant_newurl, merchartID,accountID,isLogined];
            NSString *response =[QuHaoUtil requestDb:url];
            if([response isEqualToString:@""]){
                //异常处理
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
                if(jsonObjects==nil){
                    //解析错误
                    [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
                }else{
                    [self analyzeMerchant:[jsonObjects objectForKey:@"merchant"]];
                    [self analyzePaidui:[jsonObjects objectForKey:@"haomaVO"]];
                    [self analyzeRvo:[jsonObjects objectForKey:@"rvos"]];
                }
            }

            dispatch_async(dispatch_get_main_queue(), ^{
                [hud hide:YES];
                _detailView.dataSource = self;
                _detailView.delegate = self;
                [_detailView reloadData];
                _isFirst = YES;
            });
        });
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

-(void)viewDidAppear:(BOOL)animated
{
    if ([Helper isCookie]){
        accountID = [Helper getUID];
    }
    if (![accountID isEqualToString:@""] && reservation == nil && _isFirst){
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [self reloadReversion];
            dispatch_async(dispatch_get_main_queue(), ^{
                NSIndexPath *te=[NSIndexPath indexPathForRow:1 inSection:0];
                [_detailView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:te,nil] withRowAnimation:UITableViewRowAnimationFade];
            });
        });
    }
}

//取号的点击事件
- (void)clickQuhao:(id)sender
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"HH:mm"];
    NSArray *currentDateArray = [[dateFormatter  stringFromDate:[NSDate date]] componentsSeparatedByString:@":"];
    NSArray *openArray = [single.openTime componentsSeparatedByString:@":"];
    NSArray *closeArray = [single.closeTime componentsSeparatedByString:@":"];
    //int closeH = [closeArray[0] intValue] < 7?[closeArray[0] intValue]+24:[closeArray[0] intValue];
    if(([currentDateArray[0] intValue] > [openArray[0] intValue]|| ([currentDateArray[0] intValue] == [openArray[0] intValue] && [currentDateArray[1] intValue] >= [openArray[1] intValue])) && ([currentDateArray[0] intValue] < [closeArray[0] intValue]|| ([currentDateArray[0] intValue] == [closeArray[0] intValue] && [currentDateArray[1] intValue] <= [closeArray[1] intValue]))){
        Helper *helper=[Helper new];
        if (![Helper isCookie]) {
            LoginView *loginView = [[LoginView alloc] init];
            loginView._isPopupByNotice = YES;
            helper.viewBeforeLogin = self;
            helper.viewNameBeforeLogin = @"MerchartDetail";
            loginView.helper=helper;
            loginView.hidesBottomBarWhenPushed=YES;
            [self.navigationController pushViewController:loginView animated:YES];
            return;
        }else{
            QuHaoViewController *quhao = [[QuHaoViewController alloc] init];
            quhao.merchartID = single.id;
            quhao.accountID=[Helper getUID];
            quhao.seatType=single.seatType;
            [self.navigationController pushViewController:quhao animated:YES];
        }
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"亲,商家还没营业" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }

}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

//希望开通取号
- (void)clickKt:(id)sender
{
    if([Helper isConnectionAvailable]){
        if (![Helper isCookie]) {
            Helper *helper=[Helper new];
            LoginView *loginView = [[LoginView alloc] init];
            loginView._isPopupByNotice = YES;
            helper.viewBeforeLogin = self;
            helper.viewNameBeforeLogin = @"MerchartDetail";
            loginView.helper=helper;
            loginView.hidesBottomBarWhenPushed=YES;
            [self.navigationController pushViewController:loginView animated:YES];
            return;
        }else{
            NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@",IP,openService, accountID,merchartID];
            NSString *response =[QuHaoUtil requestDb:url];
            if([response isEqualToString:@"success"]){
                [Helper showHUD2:@"建议成功" andView:self.view andSize:100];
            }else{
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//分享微博
- (void)clickShare:(id)sender
{
    ShareViewController *share = [[ShareViewController alloc] init];
    [self.navigationController pushViewController:share animated:YES];
}

//关注的点击事件
- (void)clickGz:(id)sender
{
    if([Helper isConnectionAvailable]){
        UIButton* btn=(UIButton*)sender;
        Helper *helper=[Helper new];
        if (![Helper isCookie]) {
            LoginView *loginView = [[LoginView alloc] init];
            loginView._isPopupByNotice = YES;
            helper.viewBeforeLogin = self;
            helper.viewNameBeforeLogin = @"MerchartDetail";
            loginView.helper=helper;
            loginView.hidesBottomBarWhenPushed=YES;
            [self.navigationController pushViewController:loginView animated:YES];
            return;
        }else{
            NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@&flag=%d",IP,updateAttention, accountID,merchartID,btn.tag];
            NSString *response =[QuHaoUtil requestDb:url];
            if([response isEqualToString:@"success"]){
                if(btn.tag==1){
                    single.isAttention=false;
                }else{
                    single.isAttention=true;
                }
            }
            NSIndexPath *te=[NSIndexPath indexPathForRow:0 inSection:0];
            [_detailView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:te,nil] withRowAnimation:UITableViewRowAnimationFade];
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//加载用户的座位信息
-(void)reloadReversion
{    if([Helper isConnectionAvailable]){
        NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@",IP,getReservation_url, accountID,merchartID];
        NSString *response =[QuHaoUtil requestDb:url];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                if(jsonObjects.count!=0){
                    self.reservation = [[Reservation alloc] init];
                    reservation.accountId=[[jsonObjects objectAtIndex:0] objectForKey:@"accountId"];
                    reservation.seatNumber=[[jsonObjects objectAtIndex:0]  objectForKey:@"seatNumber"];
                    reservation.myNumber=[[jsonObjects objectAtIndex:0]  objectForKey:@"myNumber"];
                    reservation.beforeYou=[[[jsonObjects objectAtIndex:0] objectForKey:@"beforeYou"] intValue];
                    reservation.merchantId=[[jsonObjects objectAtIndex:0] objectForKey:@"merchantId"];
                    reservation.currentNumber=[[jsonObjects objectAtIndex:0] objectForKey:@"currentNumber"];
                }
            }
        }
    }
}

//解析商家信息
-(void)analyzeMerchant:(NSDictionary *) jsonObjects
{
    single.id = [jsonObjects objectForKey:@"id"];
    single.name = [jsonObjects  objectForKey:@"name"];
    single.averageCost = [[jsonObjects objectForKey:@"averageCost"] floatValue];
    single.xingjiabi = [[jsonObjects objectForKey:@"xingjiabi"] floatValue];
    single.fuwu = [[jsonObjects objectForKey:@"fuwu"] floatValue];
    single.kouwei = [[jsonObjects objectForKey:@"kouwei"] floatValue];
    single.huanjing = [[jsonObjects objectForKey:@"huanjing"] floatValue];
    single.address = [jsonObjects  objectForKey:@"address"];
    single.telephone = [jsonObjects  objectForKey:@"telephone"];
    single.tags = [jsonObjects  objectForKey:@"tags"];
    single.imgUrl = [jsonObjects  objectForKey:@"merchantImage"];
    single.openTime = [jsonObjects objectForKey:@"openTime"];
    single.closeTime = [jsonObjects objectForKey:@"closeTime"];
    single.commentContent = [jsonObjects objectForKey:@"commentContent"];
    single.description = [jsonObjects objectForKey:@"description"];
    single.seatType = [jsonObjects objectForKey:@"seatType"];
    single.isAttention = [[jsonObjects objectForKey:@"isAttention"] boolValue];
    single.enable = [[jsonObjects objectForKey:@"enable"] boolValue];
    single.x = [[jsonObjects objectForKey:@"x"] doubleValue];
    single.y = [[jsonObjects objectForKey:@"y"] doubleValue];
}

//解析我的取号情况
-(void)analyzeRvo:(NSArray *) jsonObjects
{
    if(jsonObjects.count!=0){
        self.reservation = [[Reservation alloc] init];
        reservation.accountId = [[jsonObjects objectAtIndex:0] objectForKey:@"accountId"];
        reservation.seatNumber = [[jsonObjects objectAtIndex:0]  objectForKey:@"seatNumber"];
        reservation.myNumber = [[jsonObjects objectAtIndex:0]  objectForKey:@"myNumber"];
        reservation.beforeYou = [[[jsonObjects objectAtIndex:0] objectForKey:@"beforeYou"] intValue];
        reservation.merchantId = [[jsonObjects objectAtIndex:0] objectForKey:@"merchantId"];
        reservation.currentNumber = [[jsonObjects objectAtIndex:0] objectForKey:@"currentNumber"];
    }
}

//解析排队情况
-(void)analyzePaidui:(NSDictionary *) jsonObjects
{
    NSDictionary *objects=[jsonObjects objectForKey:@"haomaVOMap"];
    NSDictionary * result = nil;
    Paidu *model = nil;
    [_paiduArray removeAllObjects];
    for(int i=0; i < [single.seatType count];i++ ){
        result=[objects objectForKey:[single.seatType objectAtIndex:i]];
        if(result){
            model = [[Paidu alloc]init];
            model.currentNumber = [result objectForKey:@"currentNumber"];
            model.maxNumber = [result objectForKey:@"maxNumber"];
            model.seatNumber = [result objectForKey:@"numberOfSeat"];
            [_paiduArray addObject:model];
        }
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 9;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    int row = [indexPath row];
    if(row==0||row==6||row==7){
        return 90;
    }else if(row==3||row==4||row==5){
        return 35;
    }else if (row==8){
        return 120;
    }else if (row==1){
        return 72;
    }else{
        if ([_paiduArray count] == 0) {
            return 50;
        }else{
            return  33+[_paiduArray count]*18;
        }
    }
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor clearColor];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    NSString *CellIdentifier = [NSString stringWithFormat:@"MerchantCell%d",indexPath.row];
    UILabel *titleLabel = nil;
    UIButton *dlButton = nil;
    UILabel * tsLabel = nil;
    UILabel *seatLabel = nil;
    UILabel *currentLabel = nil;
    UILabel *myLabel = nil;
    UILabel *beforeLabel = nil;
    UILabel *pdTsLabel = nil;
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        if ([indexPath row] ==0 ) {//商家信息
            //CGSize size=CGSizeMake(kDeviceWidth,100);
            //cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"top.jpg" toSize:size]];
            //cell.selectedBackgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"cell_select_highlight.png"]];
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"top.jpg"]];
            self.egoImgView = [[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]];
            self.egoImgView.frame = CGRectMake(5, 10, 105, 80);
            [cell.contentView addSubview:self.egoImgView];
            
            titleLabel = [Helper getCustomLabel:single.name font:18 rect:CGRectMake(egoImgView.frame.origin.x+egoImgView.frame.size.width+5, 5, kDeviceWidth-110, 25)];
            [cell.contentView addSubview:titleLabel];
            
            UIButton *shareButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
            shareButton.frame = CGRectMake( kDeviceWidth-50, 30, 50, 25 );
            shareButton.titleLabel.font = [UIFont systemFontOfSize:13];
            [shareButton setTitle:@"分 享" forState:UIControlStateNormal];
            [shareButton addTarget:self action:@selector(clickShare:) forControlEvents:UIControlEventTouchUpInside];
            [cell.contentView addSubview:shareButton];
            
            dlButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
            dlButton.frame=CGRectMake(shareButton.frame.origin.x-70, 30, 60, 25 );
            dlButton.titleLabel.font = [UIFont systemFontOfSize:13];
            //[dlButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
            [dlButton addTarget:self action:@selector(clickGz:) forControlEvents:UIControlEventTouchUpInside];
            [cell.contentView addSubview:dlButton];
            
            //商家禁用 增加希望开通
            if (!single.enable){
                UIButton *ktButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
                ktButton.frame=CGRectMake(dlButton.frame.origin.x-70, 30, 60, 25 );
                [ktButton setTitle: @"希望开通" forState: UIControlStateNormal];
                //[ktButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
                ktButton.titleLabel.font = [UIFont systemFontOfSize:13.0f];
                [ktButton addTarget:self action:@selector(clickKt:) forControlEvents:UIControlEventTouchUpInside];
                [cell.contentView addSubview:ktButton];
            }
            
            UILabel *costLabel=[[UILabel alloc]initWithFrame:CGRectZero];
            costLabel.frame=CGRectMake(titleLabel.frame.origin.x+10, dlButton.frame.origin.y+dlButton.frame.size.height, titleLabel.frame.size.width, 18);
            costLabel.text=[NSString stringWithFormat:@"%@%.2lf%@%.2lf",@" 人均:¥",single.averageCost,@"    性价比:",single.xingjiabi];
            costLabel.font=[UIFont systemFontOfSize:12];
            [cell.contentView addSubview:costLabel];
            
            UILabel *_pjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
            _pjLabel.frame=CGRectMake(titleLabel.frame.origin.x+10, costLabel.frame.origin.y+costLabel.frame.size.height, titleLabel.frame.size.width, 18);
            _pjLabel.text=[NSString stringWithFormat:@"%@%.2lf%@%.2lf%@%.2lf",@" 口味:",single.kouwei,@"    环境:",single.huanjing,@"    服务:",single.fuwu];
            _pjLabel.font=[UIFont systemFontOfSize:12];
            [cell.contentView addSubview:_pjLabel];
        }else if ([indexPath row] == 3) {//商家地址
            //cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"address.jpg" toSize:CGSizeMake(kDeviceWidth-10,35)]];
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"address.jpg"]];
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(5, 7, 30, 26);
            _imgView.image= [UIImage imageNamed:@"map.png"];
            [cell.contentView addSubview:_imgView];
            
            UILabel *mapLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 地址:",single.address] font:14 rect:CGRectMake(35, 5, kDeviceWidth-60, 30)];
            [cell.contentView addSubview:mapLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 4) {//电话
            //cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"phone.jpg" toSize:CGSizeMake(kDeviceWidth-10,35)]];
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"phone.jpg"]];
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(5, 7, 30, 26);
            _imgView.image= [UIImage imageNamed:@"dh.png"];
            [cell.contentView addSubview:_imgView];
            
            NSString *value = [NSString stringWithFormat:@"%@%@",@" 电话:",[single.telephone objectAtIndex:0]];
            UILabel *phLabel = [Helper getCustomLabel:value font:14 rect:CGRectMake(35, 5, 260, 30)];
            [cell.contentView addSubview:phLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 5){
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"time.jpg"]];
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(5, 7, 30, 26);
            _imgView.image= [UIImage imageNamed:@"clock.png"];
            [cell.contentView addSubview:_imgView];
            
            NSString *value = [NSString stringWithFormat:@"%@%@ 至 %@",@" 营业时间:",single.openTime,single.closeTime];
            UILabel *sjLabel = [Helper getCustomLabel:value font:14 rect:CGRectMake(35, 5, 260, 30)];
            [cell.contentView addSubview:sjLabel];
            cell.textLabel.font = [UIFont boldSystemFontOfSize:16];
        }else if ([indexPath row] == 6){
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"tj.jpg"]];
            UILabel *nameLabel = [Helper getCustomLabel:@"  网友推荐:" font:16 rect:CGRectMake(0, 15, 80, 15)];
            [cell.contentView addSubview:nameLabel];
            
            NSString *tagStr=@"  ";
            for(int i=0;i<single.tags.count;i++){
                tagStr=[tagStr stringByAppendingString:[single.tags objectAtIndex:i]];
                if(i!=single.tags.count-1){
                    tagStr=[tagStr stringByAppendingString:@","];
                }
            }
            NSString *result=[[tagStr stringByReplacingOccurrencesOfString:@"[" withString:@"  "] stringByReplacingOccurrencesOfString:@"]" withString:@""];
            UILabel *tjLabel = [Helper getCustomLabel:result font:14 rect:CGRectMake(10, 25, kDeviceWidth-10,65)];
            [tjLabel setNumberOfLines:0];
            [cell.contentView addSubview:tjLabel];
        }else if ([indexPath row] == 7){//点评
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"dp.jpg"]];
            UILabel *nameLabel = [Helper getCustomLabel:@"  点评:" font:16 rect:CGRectMake(0, 7, 70, 15)];
            [cell.contentView addSubview:nameLabel];
            
            UILabel *dpLabel = [Helper getCustomLabel:single.commentContent font:14 rect:CGRectMake(10, 28, kDeviceWidth-10, 82)];
            [dpLabel setLineBreakMode:NSLineBreakByWordWrapping];
            [dpLabel setMinimumScaleFactor:14];
            [dpLabel setNumberOfLines:0];
            [[cell contentView] addSubview:dpLabel];
            
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 8){
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ms.jpg"]];
            UILabel *nameLabel = [Helper getCustomLabel:@"  商家描述:" font:16 rect:CGRectMake(0, 25, 100, 15)];
            [cell.contentView addSubview:nameLabel];
            
            UILabel *msLabel = [Helper getCustomLabel:single.description font:14 rect:CGRectMake(10, 25, kDeviceWidth-10,65)];
            [msLabel setNumberOfLines:0];
            [cell.contentView addSubview:msLabel];
        }

    }
    if ([indexPath row] == 0){
        if (![[Helper returnUserString:@"showImage"] boolValue]||[single.imgUrl isEqualToString:@""])
        {
            self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
        }
        else
        {
            self.egoImgView.imageURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,single.imgUrl]];
        }
        titleLabel.text = single.name;
        
        if(single.isAttention){
            [dlButton setTitle:@"取消关注" forState:UIControlStateNormal];
            dlButton.tag=1;
        }else{
            [dlButton setTitle:@"+关 注" forState:UIControlStateNormal];
            dlButton.tag=0;
        }

    }else if ([indexPath row] ==1 ){
        while ([cell.contentView.subviews lastObject] != nil)
        {
            [(UIView*)[cell.contentView.subviews lastObject] removeFromSuperview];
        }
        //CGSize size=CGSizeMake(kDeviceWidth-10,72);
        //cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"qhmk.png" toSize:size]];
        cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"qhmk"]];
        UILabel *nameLabel = [Helper getCustomLabel:@"我的取号情况:" font:16 rect:CGRectMake(5, 5, 120, 15)];
        [cell.contentView addSubview:nameLabel];
        if(reservation==nil){
            tsLabel = [Helper getCustomLabel:@"   暂无信息" font:15 rect:CGRectMake(0, 32, 100, 35)];
            tsLabel.textAlignment = NSTextAlignmentCenter;
            [cell.contentView addSubview:tsLabel];
        }else{
            NSString *value = [NSString stringWithFormat:@"%@%@",@" 座位人数:",[reservation.seatNumber description]];
            seatLabel = [Helper getCustomLabel:value font:15 rect:CGRectMake(40, 25, 130, 15)];
            [cell.contentView addSubview:seatLabel];
            
            NSString *current = [NSString stringWithFormat:@"%@%@",@" 下一号码:",[reservation.currentNumber description]];
            currentLabel = [Helper getCustomLabel:current font:15 rect:CGRectMake(175, 25, 130, 15)];
            [cell.contentView addSubview:currentLabel];
            
            NSString *my = [NSString stringWithFormat:@"%@%@",@" 我的号码:",[reservation.myNumber description]];
            myLabel = [Helper getCustomLabel:my font:15 rect:CGRectMake(40, 47, 130, 15)];
            [cell.contentView addSubview:myLabel];
            
            NSString *before = [NSString stringWithFormat:@"%@%d",@" 在你前面:",reservation.beforeYou];
            beforeLabel = [Helper getCustomLabel:before font:15 rect:CGRectMake(175, 47, 130, 15)];
            [cell.contentView addSubview:beforeLabel];
        }
    }else if ([indexPath row] ==2 ){
        while ([cell.contentView.subviews lastObject] != nil)
        {
            [(UIView*)[cell.contentView.subviews lastObject] removeFromSuperview];
        }
        //CGSize size=CGSizeMake(kDeviceWidth-10,33+[_paiduArray count]*18);
        //cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"qhmk.png" toSize:size]];
        cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"qhmk"]];
        UILabel *nameLabel = [Helper getCustomLabel:@"排队情况:" font:15 rect:CGRectMake(5, 5, 90, 15)];
        [cell.contentView addSubview:nameLabel];
        
        _sxButton=[Helper getBackBtn:@"button.png" title:@"刷 新" rect:CGRectMake(kDeviceWidth-60, 5, 40, 25 )];
        [_sxButton addTarget:self action:@selector(refreshPaidui:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:_sxButton];
        if([_paiduArray count] == 0){
            pdTsLabel = [Helper getCustomLabel:@"   暂无信息" font:14 rect:CGRectMake(0, 30, 100, 15)];
            pdTsLabel.textAlignment = NSTextAlignmentCenter;
            [cell.contentView addSubview:pdTsLabel];
        }else{
            Paidu *paidu = nil;
            for (int j=0; j<[_paiduArray count]; j++) {
                paidu=(Paidu *)_paiduArray[j];
                NSString *value = [NSString stringWithFormat:@"%@%@",@"座位人数:",paidu.seatNumber];
                UILabel *seatLabel = [Helper getCustomLabel:value font:14 rect:CGRectMake(20, 30+j*18, 80, 15)];
                [cell.contentView addSubview:seatLabel];
                
                NSString *current = [NSString stringWithFormat:@"%@%@",@"最大号码:",paidu.maxNumber];
                UILabel *currentLabel = [Helper getCustomLabel:current font:14 rect:CGRectMake(120, 30+j*18, 110, 15)];
                [cell.contentView addSubview:currentLabel];
                
                NSString *my = [NSString stringWithFormat:@"%@%@",@"下一号码:",paidu.currentNumber];
                UILabel *teamLabel = [Helper getCustomLabel:my font:14 rect:CGRectMake(220, 30+j*18, 130, 15)];
                [cell.contentView addSubview:teamLabel];
            }
        }
    }
    return cell;
}

//设置cell的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    if (row ==4) {
        [self CallPhone];
    }else if(row==3){
         [self pushMap:@"d" andNavController:self.navigationController andIsNextPage:NO];
    }else if(row==7){
        [self pushComment:@"d" andNavController:self.navigationController];
    }
}

-(void)refreshPaidui:(id)sender
{
    if([Helper isConnectionAvailable]){
        NSString *url = [NSString stringWithFormat:@"%@%@%@",IP,pStatus,merchartID];
        _sxButton.enabled = NO ;
        ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:[NSURL URLWithString:url]];
        [request setDelegate:self];
        [request setDidFailSelector:@selector(requestFailed:)];
        [request setDidFinishSelector:@selector(requestSuccess:)];
        [request startAsynchronous];
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

- (void)requestFailed:(ASIHTTPRequest *)requestNew
{
    _sxButton.enabled = YES;
    [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
}

- (void)requestSuccess:(ASIHTTPRequest *)requestNew
{
    NSError *httpError = [requestNew error];
    NSString *response = nil;
    _sxButton.enabled = YES;
    if (!httpError) {
        response = [requestNew responseString];
    }
    if([response isEqualToString:@""]){
        //异常处理
        [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
    }else{
        NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
        if(jsonObjects==nil){
            //解析错误
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            [self analyzePaidui:jsonObjects];
            NSIndexPath *te=[NSIndexPath indexPathForRow:2 inSection:0];
            [_detailView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:te,nil] withRowAnimation:UITableViewRowAnimationFade];
        }
    }
}

//拨打电话的
-(void)CallPhone
{
    UIWebView *phoneCallWebView=nil;
   
    
    NSString *phoneNum = [single.telephone objectAtIndex:0];// 电话号码
    NSURL *phoneURL = [NSURL URLWithString:[NSString stringWithFormat:@"tel:%@",phoneNum]];
    if ( !phoneCallWebView ) {
        phoneCallWebView = [[UIWebView alloc] initWithFrame:CGRectZero];// 这个webView只是一个后台的View 不需要add到页面上来  效果跟方法二一样 但是这个方法是合法的
    }
    [phoneCallWebView loadRequest:[NSURLRequest requestWithURL:phoneURL]];
}

- (void)initMapView
{
    self.mapView = [[MAMapView alloc] initWithFrame:self.view.bounds];
}

//弹出显示商家位置的地图
- (void)pushMap:(NSString *)address andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage
{
    if(NULL == self.mapView){
        [self initMapView];
    }
    BaseMapViewController *subViewController = [[MerchartLocationController alloc] init];
    subViewController.title = single.name;
    subViewController.mapView = self.mapView;
    subViewController.x = single.x;
    subViewController.y = single.y;
    [navController pushViewController:(UIViewController*)subViewController animated:YES];    
}

- (void)pushComment:(NSString *)cateType andNavController:(UINavigationController *)navController
{
    CommentViewController *comment = [[CommentViewController alloc] init];
    comment.accountOrMerchantId=single.id;
    comment.whichComment=1;
    comment.title = @"评论";
    comment.hidesBottomBarWhenPushed=YES;
    [navController pushViewController:comment animated:YES];
}

- (void)viewDidUnload
{
    single = nil;
    [super viewDidUnload];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}
@end
