//
//  CurrentDetailController.m
//  quHaoIos
//
//  Created by sam on 13-11-17.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CurrentDetailController.h"

@interface CurrentDetailController ()
@end

@implementation CurrentDetailController
@synthesize single;
@synthesize merchartID;
@synthesize isNextPage;
@synthesize reservation;
@synthesize accountID;
@synthesize egoImgView;
@synthesize youhui;

-(void)loadView
{
    
    UIView  *view=[[UIView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];
    self.view=view;
    _detailView=[[UITableView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight) style:UITableViewStylePlain];
    _detailView.dataSource=self;
    _detailView.delegate=self;
    _detailView.backgroundColor=[UIColor whiteColor];
    _detailView.indicatorStyle=UIScrollViewIndicatorStyleWhite;
    [self.view addSubview:_detailView];
    [self loadNavigationItem];
    
}

-(void)loadNavigationItem
{
    self.title = NSLocalizedString(@"商家信息", @"商家信息");
    
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    

    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@"取消号码" rect:CGRectMake( 0, 0, 60, 25 )];
    [btnButton addTarget:self action:@selector(clickCancel:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.single = [[MerchartModel alloc] init];
    NSString *isLogined = @"false";
    if ([Helper isCookie]){
        accountID =[Helper getUID];
        isLogined = @"true";
    }
    if([Helper isConnectionAvailable]){
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = NSLocalizedString(@"正在加载", nil);
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            NSString *url = [NSString stringWithFormat:@"%@%@?merchantId=%@&accountId=%@&isLogined=%@",IP,merchant_newurl, merchartID,accountID,isLogined];
            NSString *response =[QuHaoUtil requestDb:url];
            if([response isEqualToString:@""]){
                //异常处理
                hud.labelText = @"服务器错误";
            }else{
                NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
                if(jsonObjects==nil){
                    //解析错误
                    hud.labelText = @"服务器错误";
                }else{
                    [self analyzeMerchant:[jsonObjects objectForKey:@"merchant"]];
                    [self analyzeRvo:[jsonObjects objectForKey:@"rvos"]];
                }
            }
            
            url = [NSString stringWithFormat:@"%@%@?mid=%@",IP,getYouHui_url, merchartID];
            response =[QuHaoUtil requestDb:url];
            if([response isEqualToString:@""]){
                //异常处理
                hud.labelText = @"服务器错误";
            }else{
                if(![response isEqualToString:@"false"]){
                    NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
                    if(jsonObjects==nil){
                        //解析错误
                        hud.labelText = @"服务器错误";
                    }else{
                        [self analyzeYh:jsonObjects];
                    }
                }
            }
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [hud hide:YES afterDelay:0.5];
                [_detailView reloadData];
            });
        });
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
    
}

- (void)clickCancel:(id)sender
{
    if([Helper isConnectionAvailable]){
        if(youhui != nil){
            if ([Helper checkTime:reservation.created]/60 < single.checkTime) {
                NSString *str = [NSString stringWithFormat:@"由于您等待超过了%d个小时,前往商家消费会有优惠,是否继续取消？\n 优惠详情:%@",single.checkTime/60,youhui.content];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"温馨提示" message:str delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
                [alert setTag: 1];
                [alert show];
            }
        }else{
            [self realCancelNum];
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//下载
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([alertView tag] == 1 && buttonIndex == 1) {
        [self realCancelNum];
    }
}

-(void)realCancelNum
{
    NSString *url = [NSString stringWithFormat:@"%@%@?reservationId=%@",IP,cancelQuhao, reservation.id];
    NSString *response =[QuHaoUtil requestDb:url];
    if(response){
        [Helper showHUD2:@"已取消排队号码" andView:self.view andSize:100];
        [self performSelector:@selector(clickToHome:) withObject:nil afterDelay:0.5f];
    }else{
        [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
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
    single.openNum = [[jsonObjects objectForKey:@"openNum"] intValue];
    single.checkTime = [[jsonObjects objectForKey:@"checkTime"] intValue];
}

//解析我的取号情况
-(void)analyzeRvo:(NSArray *) jsonObjects
{
    if(jsonObjects.count!=0){
        self.reservation = [[Reservation alloc] init];
        reservation.id = [[jsonObjects objectAtIndex:0] objectForKey:@"id"];
        reservation.accountId = [[jsonObjects objectAtIndex:0] objectForKey:@"accountId"];
        reservation.seatNumber = [[jsonObjects objectAtIndex:0]  objectForKey:@"seatNumber"];
        reservation.myNumber = [[jsonObjects objectAtIndex:0]  objectForKey:@"myNumber"];
        reservation.beforeYou = [[[jsonObjects objectAtIndex:0] objectForKey:@"beforeYou"] intValue];
        reservation.merchantId = [[jsonObjects objectAtIndex:0] objectForKey:@"merchantId"];
        reservation.currentNumber = [[jsonObjects objectAtIndex:0] objectForKey:@"currentNumber"];
        reservation.created = [[jsonObjects objectAtIndex:0] objectForKey:@"created"];
    }
}

//解析我的取号情况
-(void)analyzeYh:(NSDictionary *) jsonObjects
{
    self.youhui = [[YouHui alloc] init];
    youhui.title = [jsonObjects objectForKey:@"title"];
    youhui.content = [jsonObjects  objectForKey:@"content"];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 9;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger row = [indexPath row];
    if(row==0||row==5||row==6){
        return 90;
    }else if(row==2||row==3||row==4){
        return 35;
    }else if (row==7){
        return 120;
    }
    return 72;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor clearColor];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CellTableIdentifier"];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"CellTabeIndentifier"];
        if ([indexPath row] ==0 ) {//商家信息
            CGSize size=CGSizeMake(kDeviceWidth,100);
            cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"top.jpg" toSize:size]];
            //cell.selectedBackgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"cell_select_highlight.png"]];
            self.egoImgView = [[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]];
            self.egoImgView.frame = CGRectMake(5, 10, 105, 80);
            [cell.contentView addSubview:self.egoImgView];
            if (![[Helper returnUserString:@"showImage"] boolValue]||[single.imgUrl isEqualToString:@""])
            {
                self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
            }
            else
            {
                self.egoImgView.imageURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,[single.imgUrl stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding ]]];
            }
            
            UILabel *_titleLabel = [Helper getCustomLabel:single.name font:18 rect:CGRectMake(egoImgView.frame.origin.x+egoImgView.frame.size.width+5, 5, kDeviceWidth-110, 25)];
            [cell.contentView addSubview:_titleLabel];
            
            UILabel *_costLabel=[[UILabel alloc]initWithFrame:CGRectZero];
            _costLabel.frame=CGRectMake(_titleLabel.frame.origin.x+10, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+10, _titleLabel.frame.size.width, 20);
            _costLabel.text=[NSString stringWithFormat:@"%@%.2lf%@%.2lf",@" 人均:¥",single.averageCost,@"    性价比:",single.xingjiabi];
            _costLabel.font=[UIFont systemFontOfSize:12];
            [cell.contentView addSubview:_costLabel];
            
            UILabel *_pjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
            _pjLabel.frame=CGRectMake(_titleLabel.frame.origin.x+10, _costLabel.frame.origin.y+_costLabel.frame.size.height, _titleLabel.frame.size.width, 20);
            _pjLabel.text=[NSString stringWithFormat:@"%@%.2lf%@%.2lf%@%.2lf",@" 口味:",single.kouwei,@"    环境:",single.huanjing,@"    服务:",single.fuwu];
            _pjLabel.font=[UIFont systemFontOfSize:12];
            [cell.contentView addSubview:_pjLabel];
        }else if ([indexPath row] ==1 ) { //取号情况的
            //CGSize size=CGSizeMake(320,75);
            //cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"qhqk.jpg" toSize:size]];
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"qhmk"]];
            UILabel *nameLabel = [Helper getCustomLabel:@"我的取号情况:" font:16 rect:CGRectMake(5, 5, 120, 15)];
            [cell.contentView addSubview:nameLabel];
            
            if(reservation == nil){
                UILabel *seatLabel = [Helper getCustomLabel:@"   暂无信息" font:15 rect:CGRectMake(0, 32, 100, 35)];
                seatLabel.textAlignment = NSTextAlignmentCenter;
                [cell.contentView addSubview:seatLabel];
            }else{
                NSString *value = [NSString stringWithFormat:@"%@%@",@" 座位人数:",[reservation.seatNumber description]];
                UILabel *seatLabel = [Helper getCustomLabel:value font:15 rect:CGRectMake(40, 30, 130, 15)];
                [cell.contentView addSubview:seatLabel];
                
                NSString *current = [NSString stringWithFormat:@"%@%@",@" 当前号码:",[reservation.currentNumber description]];
                UILabel *currentLabel = [Helper getCustomLabel:current font:15 rect:CGRectMake(175, 30, 130, 15)];
                [cell.contentView addSubview:currentLabel];
                
                NSString *my = [NSString stringWithFormat:@"%@%@",@" 我的号码:",[reservation.myNumber description]];
                UILabel *teamLabel = [Helper getCustomLabel:my font:15 rect:CGRectMake(40, 52, 130, 15)];
                [cell.contentView addSubview:teamLabel];
                
                NSString *before = [NSString stringWithFormat:@"%@%d",@" 在你前面:",reservation.beforeYou];
                UILabel *beforeLabel = [Helper getCustomLabel:before font:15 rect:CGRectMake(175, 52, 130, 15)];
                [cell.contentView addSubview:beforeLabel];
            }
        }else if ([indexPath row] == 2) {//商家地址
            cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"address.jpg" toSize:CGSizeMake(kDeviceWidth-10,35)]];
            
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(5, 7, 30, 26);
            _imgView.image= [UIImage imageNamed:@"map.png"];
            [cell.contentView addSubview:_imgView];
            
            UILabel *mapLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 地址:",single.address] font:14 rect:CGRectMake(35, 5, kDeviceWidth-60, 30)];
            [cell.contentView addSubview:mapLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 3) {//电话
            cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"phone.jpg" toSize:CGSizeMake(kDeviceWidth-10,35)]];
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(5, 7, 30, 26);
            _imgView.image= [UIImage imageNamed:@"dh.png"];
            [cell.contentView addSubview:_imgView];
            
            NSString *value = [NSString stringWithFormat:@"%@%@",@" 电话:",[single.telephone objectAtIndex:0]];
            UILabel *phLabel = [Helper getCustomLabel:value font:14 rect:CGRectMake(35, 5, 260, 30)];
            [cell.contentView addSubview:phLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 4){
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"time.jpg"]];
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(5, 7, 30, 26);
            _imgView.image= [UIImage imageNamed:@"clock.png"];
            [cell.contentView addSubview:_imgView];
            
            if(single.openTime == nil || [single.openTime isEqualToString:@""]){
                NSString *value = [NSString stringWithFormat:@"%@%@",@" 营业时间:",@"暂无"];
                UILabel *sjLabel = [Helper getCustomLabel:value font:14 rect:CGRectMake(35, 5, 260, 30)];
                [cell.contentView addSubview:sjLabel];
            }else{
                NSString *value = [NSString stringWithFormat:@"%@%@ 至 %@",@" 营业时间:",single.openTime,single.closeTime];
                UILabel *sjLabel = [Helper getCustomLabel:value font:14 rect:CGRectMake(35, 5, 260, 30)];
                [cell.contentView addSubview:sjLabel];
            }
            cell.textLabel.font = [UIFont boldSystemFontOfSize:16];
        }else if ([indexPath row] == 5){
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
            UILabel *tjLabel = [Helper getCustomLabel:result font:14 rect:CGRectMake(10, 25, 295,65)];
            [tjLabel setNumberOfLines:0];
            [cell.contentView addSubview:tjLabel];
        }else if ([indexPath row] == 6){//点评
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"dp.jpg"]];
            UILabel *nameLabel = [Helper getCustomLabel:@"  点评:" font:16 rect:CGRectMake(0, 7, 70, 15)];
            [cell.contentView addSubview:nameLabel];
            
            UILabel *dpLabel = [Helper getCustomLabel:single.commentContent font:14 rect:CGRectMake(10, 28, 295, 82)];
            [dpLabel setLineBreakMode:NSLineBreakByWordWrapping];
            [dpLabel setMinimumScaleFactor:14];
            [dpLabel setNumberOfLines:0];
            [[cell contentView] addSubview:dpLabel];
            
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 7){
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ms.jpg"]];
            UILabel *nameLabel = [Helper getCustomLabel:@"  商家描述:" font:16 rect:CGRectMake(0, 25, 100, 15)];
            [cell.contentView addSubview:nameLabel];
            if(single.description!=nil && ![single.description isEqualToString:@""]){
                UILabel *msLabel = [Helper getCustomLabel:single.description font:14 rect:CGRectMake(10, 40, kDeviceWidth-50,65)];
                [msLabel setNumberOfLines:0];
                [cell.contentView addSubview:msLabel];
                [Helper arrowStyle:cell];
            }else{
                UILabel *msLabel = [Helper getCustomLabel:@"暂无介绍" font:14 rect:CGRectMake(10, 40, kDeviceWidth-50,65)];
                [msLabel setNumberOfLines:0];
                [cell.contentView addSubview:msLabel];
            }
        }
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    }
    return cell;
}

//设置cell的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSInteger row = [indexPath row];
    if (row ==3) {
        [self CallPhone];
    }else if(row==2){
        [self pushMap:@"d" andNavController:self.navigationController andIsNextPage:NO];
    }else if(row==6){
        [self pushComment:@"d" andNavController:self.navigationController];
    }
}

//拨打电话的
-(void)CallPhone
{
    if([[single.telephone objectAtIndex:0] isEqualToString:@""]){
        return;
    }
    UIWebView *phoneCallWebView=nil;
    NSString *phoneNum = [single.telephone objectAtIndex:0];// 电话号码
    NSURL *phoneURL = [NSURL URLWithString:[NSString stringWithFormat:@"tel:%@",phoneNum]];
    if ( !phoneCallWebView ) {
        phoneCallWebView = [[UIWebView alloc] initWithFrame:CGRectZero];// 这个webView只是一个后台的View 不需要add到页面上来  效果跟方法二一样 但是这个方法是合法的
    }
    [phoneCallWebView loadRequest:[NSURLRequest requestWithURL:phoneURL]];
    [self.view addSubview:phoneCallWebView];
}

//弹出显示商家位置的地图
- (void)pushMap:(NSString *)address andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage
{
    MapViewController *subViewController = [[MapViewController alloc] init];
    subViewController.title = single.name;
    //subViewController.mapView = self.mapView;
    subViewController.x = single.x;
    subViewController.y = single.y;
    subViewController.name = single.name;
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

- (void)pushSjms:(UINavigationController *)navController
{
    SjmsViewController *sjmsView = [[SjmsViewController alloc] init];
    sjmsView.sjms = single.description;
    sjmsView.hidesBottomBarWhenPushed=YES;
    [navController pushViewController:sjmsView animated:YES];
}

- (void)viewDidUnload
{
    single = nil;
    [super viewDidUnload];
}

@end