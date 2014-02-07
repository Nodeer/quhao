//
//  CurrentDetailController.m
//  quHaoIos
//
//  Created by sam on 13-11-17.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CurrentDetailController.h"

@interface CurrentDetailController ()
@property (nonatomic, strong) MAMapView *mapView;
@end

@implementation CurrentDetailController
@synthesize single;
@synthesize merchartID;
@synthesize isNextPage;
@synthesize mapView = _mapView;
@synthesize reservation;
@synthesize accountID;
@synthesize egoImgView;


-(void)loadView
{
    self.reservation = [[Reservation alloc] init];
    
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
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.single = [[MerchartModel alloc] init];
    if([Helper isConnectionAvailable]){
        
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = NSLocalizedString(@"正在加载", nil);
        hud.square = YES;
        NSString *url = [NSString stringWithFormat:@"%@%@?id=%@",[Helper getIp],merchant_url, merchartID];
        NSString *response =[QuHaoUtil requestDb:url];
        [hud hide:YES];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                single.id=[jsonObjects objectForKey:@"id"];
                single.name=[jsonObjects  objectForKey:@"name"];
                single.averageCost=[[jsonObjects objectForKey:@"averageCost"] floatValue];
                single.xingjiabi=[[jsonObjects objectForKey:@"xingjiabi"] floatValue];
                single.fuwu=[[jsonObjects objectForKey:@"fuwu"] floatValue];
                single.kouwei=[[jsonObjects objectForKey:@"kouwei"] floatValue];
                single.huanjing=[[jsonObjects objectForKey:@"huanjing"] floatValue];
                single.address=[jsonObjects  objectForKey:@"address"];
                single.telephone=[jsonObjects  objectForKey:@"telephone"];
                single.tags=[jsonObjects  objectForKey:@"tags"];
                single.imgUrl=[jsonObjects  objectForKey:@"merchantImage"];
                single.openTime=[jsonObjects objectForKey:@"openTime"];
                single.closeTime=[jsonObjects objectForKey:@"closeTime"];
                single.commentContent=[jsonObjects objectForKey:@"commentContent"];
                single.description=[jsonObjects objectForKey:@"description"];
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
    
    [self reloadReversion];
    [_detailView reloadData];
    [self initMapView];
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

//加载用户的座位信息
-(void)reloadReversion
{
    if([Helper isConnectionAvailable]){
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = NSLocalizedString(@"正在加载", nil);
        hud.square = YES;
        
        NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@",[Helper getIp],getReservation_url, accountID,merchartID];
        NSString *response =[QuHaoUtil requestDb:url];
        [hud hide:YES];
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
                    reservation.accountId=[[jsonObjects objectAtIndex:0] objectForKey:@"accountId"];
                    reservation.seatNumber=[[jsonObjects objectAtIndex:0]  objectForKey:@"seatNumber"];
                    reservation.myNumber=[[jsonObjects objectAtIndex:0]  objectForKey:@"myNumber"];
                    reservation.beforeYou=[[jsonObjects objectAtIndex:0] objectForKey:@"beforeYou"];
                    reservation.merchantId=[[jsonObjects objectAtIndex:0] objectForKey:@"merchantId"];
                    reservation.currentNumber=[[jsonObjects objectAtIndex:0] objectForKey:@"currentNumber"];
                }
            }
        }
        [hud hide:YES];
    }else{
        //[Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 9;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    int row = [indexPath row];
    if(row==0||row==5||row==6){
        return 100;
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
            CGSize size=CGSizeMake(320,100);
            cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"top.jpg" toSize:size]];
            //cell.selectedBackgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"cell_select_highlight.png"]];
            self.egoImgView = [[EGOImageView alloc] initWithFrame:CGRectMake(5, 10, 105, 80)];
            self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
            [cell.contentView addSubview:self.egoImgView];
            if ([single.imgUrl isEqualToString:@""])
            {
                self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
            }
            else
            {
                self.egoImgView.imageURL = [NSURL URLWithString:single.imgUrl];
            }
            
            UILabel *_titleLabel = [Helper getCustomLabel:single.name font:18 rect:CGRectMake(egoImgView.frame.origin.x+egoImgView.frame.size.width+5, 10, 190, 30)];
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
            CGSize size=CGSizeMake(320,75);
            cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"qhqk.jpg" toSize:size]];
            UILabel *nameLabel = [Helper getCustomLabel:@"  取号情况:" font:16 rect:CGRectMake(0, 5, 80, 15)];
            [cell.contentView addSubview:nameLabel];
            
            if(reservation.myNumber==nil||reservation.myNumber==@""){
                UILabel *seatLabel = [Helper getCustomLabel:@"   暂无信息" font:15 rect:CGRectMake(0, 32, 100, 35)];
                seatLabel.textAlignment = NSTextAlignmentCenter;
                [cell.contentView addSubview:seatLabel];
            }else{
                NSString *current = [NSString stringWithFormat:@"%@%@",@" 当前号码:",[reservation.currentNumber description]];
                UILabel *currentLabel = [Helper getCustomLabel:current font:15 rect:CGRectMake(8, 36, 110, 15)];
                [cell.contentView addSubview:currentLabel];
                
                NSString *my = [NSString stringWithFormat:@"%@%@",@" 我的号码:",[reservation.myNumber description]];
                UILabel *teamLabel = [Helper getCustomLabel:my font:15 rect:CGRectMake(115, 36, 110, 15)];
                [cell.contentView addSubview:teamLabel];
                
                NSString *value = [NSString stringWithFormat:@"%@%@",@" 座位人数:",[reservation.seatNumber description]];
                UILabel *seatLabel = [Helper getCustomLabel:value font:15 rect:CGRectMake(225, 36, 90, 15)];
                [cell.contentView addSubview:seatLabel];
            }
        }else if ([indexPath row] == 2) {//商家地址
            cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"address.jpg" toSize:CGSizeMake(320,35)]];
            
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(8, 7, 21, 26);
            _imgView.image= [UIImage imageNamed:@"map.png"];
            [cell.contentView addSubview:_imgView];
            
            UILabel *mapLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 地址:",single.address] font:14 rect:CGRectMake(30, 5, 260, 30)];
            [cell.contentView addSubview:mapLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 3) {//电话
            cell.backgroundView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"phone.jpg" toSize:CGSizeMake(320,35)]];
            UIImageView *_imgView=[[UIImageView alloc] initWithFrame:CGRectZero];
            _imgView.backgroundColor=[UIColor clearColor];
            _imgView.frame=CGRectMake(8, 7, 21, 26);
            _imgView.image= [UIImage imageNamed:@"dh.png"];
            [cell.contentView addSubview:_imgView];
            
            NSString *value = [NSString stringWithFormat:@"%@%@",@" 电话:",[single.telephone objectAtIndex:0]];
            UILabel *phLabel = [Helper getCustomLabel:value font:14 rect:CGRectMake(30, 5, 260, 30)];
            [cell.contentView addSubview:phLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] == 4){
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"time.jpg"]];
            cell.textLabel.text =[NSString stringWithFormat:@"%@%@至%@",@"营业时间:",single.openTime,single.openTime];
            cell.textLabel.font = [UIFont boldSystemFontOfSize:16];
        }else if ([indexPath row] == 5){
            cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"tj.jpg"]];
            UILabel *nameLabel = [Helper getCustomLabel:@"  网友推荐:" font:16 rect:CGRectMake(0, 5, 80, 15)];
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
            
            UILabel *msLabel = [Helper getCustomLabel:single.description font:14 rect:CGRectMake(10, 25, 295,65)];
            [msLabel setNumberOfLines:0];
            [cell.contentView addSubview:msLabel];
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
    NSString *phoneNum = [single.telephone objectAtIndex:0];// 电话号码
    UIWebView *phoneCallWebView=nil;
    NSURL *phoneURL = [NSURL URLWithString:[NSString stringWithFormat:@"tel:%@",phoneNum]];
    
    if ( !phoneCallWebView ) {
        // 这个webView只是一个后台的容易 不需要add到页面上来  效果跟方法二一样 但是这个方法是合法的
        phoneCallWebView = [[UIWebView alloc] initWithFrame:CGRectZero];
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
    BaseMapViewController *subViewController = [[MerchartLocationController alloc] init];
    subViewController.title = single.name;
    subViewController.mapView = self.mapView;
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

@end