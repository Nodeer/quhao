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
-(void)loadView
{
    self.reservation = [[Reservation alloc] init];
    UIView  *view=[[UIView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];
    self.view=view;
    
    _quHaoView=[[UITableView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight) style:UITableViewStylePlain];
    _quHaoView.dataSource=self;
    _quHaoView.delegate=self;
    _quHaoView.backgroundColor=[UIColor whiteColor];
    _quHaoView.indicatorStyle=UIScrollViewIndicatorStyleWhite;
    [self.view addSubview:_quHaoView];
    [self loadNavigationItem];
    
    showList = NO; //默认不显示下拉框
    
    [self reloadReversion];
    [self reloadCurrent];
}

-(void)loadNavigationItem
{    
    //self.title = NSLocalizedString(@"取号情况", @"取号情况");
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    //添加拿号按钮
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 拿 号" rect:CGRectMake( 0, 7, 50, 30 )];
    [btnButton addTarget:self action:@selector(clickNahao:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

//拿号的点击事件
- (void)clickNahao:(id)sender
{
    [self reloadView];
}

//加载用户的座位信息
-(void)reloadReversion
{
    if ([Helper isConnectionAvailable]){
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = NSLocalizedString(@"正在加载", nil);
        hud.square = YES;
        
        NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@",[Helper getIp],getReservation_url, accountID,merchartID];
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
                    reservation.accountId=[[jsonObjects objectAtIndex:0]  objectForKey:@"accountId"];
                    reservation.seatNumber=[[jsonObjects objectAtIndex:0]  objectForKey:@"seatNumber"];
                    reservation.myNumber=[[jsonObjects objectAtIndex:0]  objectForKey:@"myNumber"];
                    reservation.beforeYou=[[jsonObjects objectAtIndex:0] objectForKey:@"beforeYou"];
                    reservation.currentNumber=[[jsonObjects objectAtIndex:0] objectForKey:@"currentNumber"];
                    reservation.merchantId=[[jsonObjects objectAtIndex:0] objectForKey:@"merchantId"];
                }
                [hud hide:YES];
            }
        }
    }
    else
    {
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
    [_quHaoView reloadData];
}

//拿号的请求方法
-(void)reloadView
{
    if(reservation.seatNumber==nil||reservation.seatNumber==@""){
        if([Helper isConnectionAvailable]){
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            hud.labelText = NSLocalizedString(@"正在加载", nil);
            hud.square = YES;
            NSString *seatNumber=nil;
            if(selectView!=nil){
                NSInteger row =[selectView selectedRowInComponent:0];
                seatNumber= [pickerData objectAtIndex:row];
            }else{
                seatNumber=@"2";
            }
            NSString *url = [NSString stringWithFormat:@"%@%@?accountId=%@&mid=%@&seatNumber=%@",[Helper getIp],nahao_url, accountID,merchartID,seatNumber];
            NSString *response =[QuHaoUtil requestDb:url];
            NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
            
            reservation.accountId=[jsonObjects  objectForKey:@"accountId"];
            reservation.seatNumber=[jsonObjects  objectForKey:@"seatNumber"];
            reservation.myNumber=[jsonObjects  objectForKey:@"myNumber"];
            reservation.currentNumber=[jsonObjects  objectForKey:@"currentNumber"];
            reservation.beforeYou=[jsonObjects objectForKey:@"beforeYou"];
            reservation.merchantId=[jsonObjects objectForKey:@"merchantId"];
            NSString *tip=[jsonObjects objectForKey:@"tipValue"];
            [hud hide:YES];
            if([tip isEqualToString:@"NO_MORE_JIFEN"]){
                [Helper showHUD2:@"亲,没有积分可用了" andView:self.view andSize:100];
            }else{
                [_quHaoView reloadData];
            }
        }else{
            [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
        }
    }else{
        [Helper showHUD2:@"您已经拿过号了" andView:self.view andSize:100];
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 4;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 55;
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
        if ([indexPath row] ==0 ) {//座位人数
            NSString *seat=nil;
            if(reservation.seatNumber==nil||reservation.seatNumber==@""){
                seat=@"2";
            }else{
                seat=[reservation.seatNumber description];
            }
            UILabel *nameLabel = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 座位人数:    ",seat] font:18 rect:CGRectMake(5, 18, 150, 30)];
            [cell.contentView addSubview:nameLabel];
            [Helper arrowStyle:cell];
        }else if ([indexPath row] ==1 ) { //当前号码
            UILabel *label = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 当前号码:    ",[reservation.currentNumber description]] font:18 rect:CGRectMake(5, 18, 150, 30)];
            [cell.contentView addSubview:label];
        }else if ([indexPath row] == 2) {//我的号码
            if(reservation.accountId!=nil){
                UILabel *label = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 我的号码:    ",[reservation.myNumber description]] font:18 rect:CGRectMake(5, 18, 150, 30)];
                [cell.contentView addSubview:label];
            }
        }else if ([indexPath row] == 3) {//在你前面
            if(reservation.accountId!=nil){
                UILabel *label = [Helper getCustomLabel:[NSString stringWithFormat:@"%@%@",@" 在你前面:    ",[reservation.beforeYou description]] font:18 rect:CGRectMake(5, 18, 150, 30)];
                [cell.contentView addSubview:label];
            }
        }
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    }
    return cell;
}

-(void)dropdown{
    selectView = [[UIPickerView alloc] initWithFrame:CGRectMake(0, 230, 320, 150)];
    // 指定Delegate
    selectView.delegate=self;
    // 显示选中框
    showList = YES; //默认不显示下拉框
    selectView.showsSelectionIndicator=YES;
    [self.view addSubview:selectView];
    pickerData = seatType;
}

#pragma mark Picker Date Source Methods
//返回显示的列数
-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

//返回当前列显示的行数
-(NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [pickerData count];
}

#pragma mark Picker Delegate Methods

//返回当前行的内容,此处是将数组中数值添加到滚动的那个显示栏上
-(NSString*)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [pickerData objectAtIndex:row];
}
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)componen
{
    [self reloadCurrent];
}

//加载用户的座位的信息
-(void)reloadCurrent
{
    //用户拿过号以后就不请求服务器了
    if(reservation.accountId==nil){
        if([Helper isConnectionAvailable]){
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            //hud.mode = MBProgressHUDModeText;
            hud.labelText = NSLocalizedString(@"正在加载", nil);
            hud.square = YES;
            NSString *seatNumber=nil;
            if(selectView!=nil){
                NSInteger row =[selectView selectedRowInComponent:0];
                seatNumber= [pickerData objectAtIndex:row];
            }else{
                seatNumber=@"2";
            }
            NSString *url = [NSString stringWithFormat:@"%@%@?id=%@&seatNo=%@",[Helper getIp],getCurrentNo_url,merchartID,seatNumber];
            NSString *response =[QuHaoUtil requestDb:url];
            if([response isEqualToString:@""]){
                //异常处理
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                reservation.currentNumber=response;
                
            }
            [hud hide:YES];
        }else{
            [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
        }
        [_quHaoView reloadData];
    }
}

//设置cell的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    if (row ==0) {
        [self dropdown];
    }
}
@end
