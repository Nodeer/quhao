//
//  QuHaoViewController.m
//  quHaoIos
//
//  Created by sam on 13-9-22.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "QuHaoViewController.h"

@interface QuHaoViewController ()

@end

@implementation QuHaoViewController
@synthesize reservation;
@synthesize merchartID;
@synthesize accountID;
@synthesize seatType;
@synthesize popView;
@synthesize coverView;
- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    //button
    UIButton *backButton = [Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    //加载取号信息
    [self reloadReversion];
   
    //默认不显示下拉框
    _showList = NO;
    //创建view上的座位信息
    [self createCurrentView];

    coverView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight)];
    coverView.hidden = YES;
    coverView.backgroundColor = [UIColor blackColor];
    coverView.alpha = 0.3;
    [self.view addSubview:coverView];
    
    //创建下拉列表的view
    popView = [[UITableView alloc] initWithFrame:CGRectMake(110, 55, 100 ,120)];
    popView.delegate = self;
    popView.dataSource = self;
    popView.backgroundColor = [ UIColor whiteColor
                               ];
    popView.separatorColor = [UIColor colorWithWhite:1 alpha:.2];
#if IOS7_SDK_AVAILABLE
    if([popView respondsToSelector:@selector(setSeparatorInset:)]){
        [popView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    popView.indicatorStyle = UIScrollViewIndicatorStyleBlack;
    popView.hidden = YES;
    [self.view addSubview:popView];
    
    if(reservation == nil){
        [self reloadCurrent];
    }
}

//创建view上的座位信息
-(void)createCurrentView
{
    //背景
    UIImageView *bgImgView = [[UIImageView alloc] initWithImage:[Helper reSizeImage:@"qhmk.png" toSize:CGSizeMake(kDeviceWidth,75)]];
    bgImgView.frame = CGRectMake(5, 10, kDeviceWidth-10, 75);
    [self.view addSubview:bgImgView];
    
    NSString *seat = nil;
    if(reservation == nil){
        seat = [self.seatType objectAtIndex:0];
    }else{
        seat = [reservation.seatNumber description];
    }
    [self.view addSubview:[Helper getCustomLabel:@" 座位人数:   " font:18 rect:CGRectMake(10, 18, 110, 30)]];
    
    _seatNumber = [Helper getCustomLabel:seat font:18 rect:CGRectMake(113, 18, 45, 30)];
    [self.view addSubview:_seatNumber];
    
    NSString *num=nil;
    if(reservation == nil){
        num = @"0";
    }else{
        num = [reservation.currentNumber description];
    }
    _currlabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 下一号码:   ",num] font:18 rect:CGRectMake(kDeviceWidth-140 ,18 ,140 ,30)];
    [self.view addSubview:_currlabel];
    
    if(reservation.accountId!=nil){
        UILabel *mylabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 我的号码:    ",[reservation.myNumber description]] font:18 rect:CGRectMake(10, _currlabel.frame.origin.y+30, 160, 30)];
        [self.view addSubview:mylabel];
        
        UILabel *belabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%d",@" 在你前面:   ",reservation.beforeYou ]font:18 rect:CGRectMake(_currlabel.frame.origin.x, _currlabel.frame.origin.y+30, 140, 30)];
        [self.view addSubview:belabel];
    }
    
    if(reservation == nil){
        
        UIImage *image = [UIImage   imageNamed:@"arrow_down.png"];
        _arrowButton= [UIButton buttonWithType:UIButtonTypeCustom];
        _arrowButton.frame = CGRectMake(140, 22, image.size.width+5, image.size.height+5);
        [_arrowButton setBackgroundImage:image forState:UIControlStateNormal];
        [_arrowButton addTarget:self action:@selector(dropdown:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:_arrowButton];
        
        UIImage *btnImage = [UIImage   imageNamed:@"max_btn.png"];
        _nahaoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _nahaoBtn.frame = CGRectMake(10, 100, 300, 30);
        [_nahaoBtn setBackgroundImage:btnImage forState:UIControlStateNormal];
        [_nahaoBtn setTitle: @"拿 号" forState: UIControlStateNormal];
        [_nahaoBtn addTarget:self action:@selector(clickNahao:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:_nahaoBtn];
    }
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

//拿号的点击事件
- (void)clickNahao:(id)sender
{
    _nahaoBtn.hidden = YES;
    _arrowButton.hidden = YES;
    [self reloadView];
}

//加载用户的座位信息
-(void)reloadReversion
{
    if ([Helper isConnectionAvailable]){
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = NSLocalizedString(@"正在加载", nil);
        hud.square = YES;
        
        NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@",IP,getReservation_url, accountID,merchartID];
        NSString *response = [QuHaoUtil requestDb:url];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects = [QuHaoUtil analyseData:response];
            if(jsonObjects == nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                if(jsonObjects.count!=0){
                    self.reservation = [[Reservation alloc] init];
                    reservation.accountId = [[jsonObjects objectAtIndex:0]  objectForKey:@"accountId"];
                    reservation.seatNumber = [[jsonObjects objectAtIndex:0]  objectForKey:@"seatNumber"];
                    reservation.myNumber = [[jsonObjects objectAtIndex:0]  objectForKey:@"myNumber"];
                    reservation.beforeYou = [[[jsonObjects objectAtIndex:0] objectForKey:@"beforeYou"] intValue];
                    reservation.currentNumber = [[jsonObjects objectAtIndex:0] objectForKey:@"currentNumber"];
                    reservation.merchantId = [[jsonObjects objectAtIndex:0] objectForKey:@"merchantId"];
                }
                [hud hide:YES];
            }
        }
    }
    else
    {
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
    //[self.view setNeedsDisplay];
}

//拿号的请求方法
-(void)reloadView
{
    if(reservation == nil){
        if([Helper isConnectionAvailable]){
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            hud.labelText = NSLocalizedString(@"正在加载", nil);
            hud.square = YES;
           
            NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@&seatNumber=%@",IP,nahao_url, accountID,merchartID,_seatNumber.text];
            NSString *response = [QuHaoUtil requestDb:url];
            NSDictionary *jsonObjects = [QuHaoUtil analyseDataToDic:response];
            NSString *tip = [jsonObjects objectForKey:@"tipValue"];
            [hud hide:YES];
            if([tip isEqualToString:@"NO_MORE_JIFEN"]){
                [Helper showHUD2:@"亲,没有积分可用了" andView:self.view andSize:100];
                _nahaoBtn.hidden = NO;
            }else{
                self.reservation = [[Reservation alloc] init];
                reservation.accountId = [jsonObjects  objectForKey:@"accountId"];
                reservation.seatNumber = [jsonObjects  objectForKey:@"seatNumber"];
                reservation.myNumber = [[jsonObjects  objectForKey:@"myNumber"] description];
                reservation.currentNumber = [jsonObjects  objectForKey:@"currentNumber"];
                reservation.beforeYou = [[jsonObjects objectForKey:@"beforeYou"] intValue];
                reservation.merchantId = [jsonObjects objectForKey:@"merchantId"];

                if(reservation.accountId!=nil){
                    UILabel *mylabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 我的号码:    ",[reservation.myNumber description]] font:18 rect:CGRectMake(10, _currlabel.frame.origin.y+30, 160, 30)];
                    [self.view addSubview:mylabel];
                    
                    UILabel *belabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%d",@" 在你前面:   ",reservation.beforeYou ]font:18 rect:CGRectMake(_currlabel.frame.origin.x, _currlabel.frame.origin.y+30, 140, 30)];
                    [self.view addSubview:belabel];
                }
                _currlabel.text = [NSString stringWithFormat:@"%@%@",@" 下一号码:   ",reservation.currentNumber];

                if(reservation.beforeYou<=5){
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"恭喜：取号成功，由于在你前面排队的不多于5桌，为了避免排队号过期，请抓紧时间前往商家。" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                    [alert show];
                }else{
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"恭喜：取号成功，当你的排号前还剩5桌时，我们会用短信通知到你，继续享受你的免排队时间吧。" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                    [alert show];
                }

            }
        }else{
            [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
            _nahaoBtn.hidden = NO;
        }
    }else{
        [Helper showHUD2:@"亲,您已经拿过号了" andView:self.view andSize:100];
    }
}

#pragma mark 加载用户的座位的信息
-(void)reloadCurrent
{
    //用户拿过号以后就不请求服务器了
    if(reservation.accountId == nil){
        if([Helper isConnectionAvailable]){
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            //hud.mode = MBProgressHUDModeText;
            hud.labelText = NSLocalizedString(@"正在加载", nil);
            hud.square = YES;
            NSString *url = [NSString stringWithFormat:@"%@%@?id=%@&seatNo=%@",IP,getCurrentNo_url,merchartID,_seatNumber.text];
            NSString *response = [QuHaoUtil requestDb:url];
            if([response isEqualToString:@""]){
                //异常处理
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                _currlabel.text = [NSString stringWithFormat:@"%@%@",@" 下一号码:   ",response];

            }
            [hud hide:YES];
        }else{
            [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
        }
        //[self.view setNeedsDisplay];
    }
}

-(void)dropdown:(id)sender{
    if (_showList) {//如果下拉框已显示，什么都不做
        return;
    }else {//如果下拉框尚未显示，则进行显示
        //把dropdownList放到前面，防止下拉框被别的控件遮住
        [popView.superview bringSubviewToFront:popView];
        coverView.hidden = NO;
        popView.hidden = NO;
        _showList = YES;//显示下拉框

        CGRect frame = popView.frame;
        frame.size.height = 0;
        popView.frame = frame;
        frame.size.height = 120;
        [UIView beginAnimations:@"ResizeForKeyBoard" context:nil];
        [UIView setAnimationCurve:UIViewAnimationCurveLinear];
        popView.frame = frame;
        [UIView commitAnimations];
    }
}

#pragma mark  popView的代码加载
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [seatType count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"popCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] ;
    }
    
    cell.textLabel.text = [seatType objectAtIndex:[indexPath row]];
    cell.textLabel.font = [UIFont systemFontOfSize:13.0f];
    cell.accessoryType  = UITableViewCellAccessoryNone;
    //cell.selectionStyle = UITableViewCellSelectionStyleGray;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 30;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    _showList = NO;
    coverView.hidden = YES;
    popView.hidden = YES;
    _seatNumber.text = [seatType objectAtIndex:[indexPath row]];
    
    [self reloadCurrent];
}

-(void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch  locationInView:coverView];
    if (!(point.x >= 100 && point.x<=200 && point.y >= 45 && point.y <=165)) {
        _showList = NO;
        coverView.hidden = YES;
        popView.hidden = YES;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}
@end
