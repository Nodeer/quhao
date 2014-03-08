//
//  MerchartHistoryController.m
//  quHaoIos
//
//  Created by sam on 13-10-22.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "MerchartHistoryController.h"

@interface MerchartHistoryController ()

@end

@implementation MerchartHistoryController

-(void)loadNavigationItem
{   
    self.tabBarItem.title=@"历史取号情况";
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;    
}
- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //添加上面的导航
    [self loadNavigationItem];
    _reservationArray = [[NSMutableArray alloc] initWithCapacity:20];
    _reloading = NO;
    _pageIndex=1;
}

-(void)viewDidAppear:(BOOL)animated
{
    [_reservationArray removeAllObjects];
    _reservationArray = [[NSMutableArray alloc] initWithCapacity:20];
    
    MBProgressHUD *HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:HUD];
    //如果设置此属性则当前的view置于后台
    HUD.dimBackground = YES;
    //设置对话框文字
    HUD.labelText = @"正在加载...";
    //显示对话框
    [HUD showAnimated:YES whileExecutingBlock:^{
        [self requestData:[NSString stringWithFormat:@"%@%@%@",[Helper getIp],history_list_url,self.accouId] withPage:_pageIndex];
        [self.tableView reloadData];
    } completionBlock:^{
        //操作执行完后取消对话框
        [HUD removeFromSuperview];
        [self addFooter];
    }];
}

//上拉加载更多
- (void)addFooter
{
    MJRefreshFooterView *footer = [MJRefreshFooterView footer];
    footer.scrollView = self.tableView;
    footer.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        _prevItemCount = [_reservationArray count];
        ++_pageIndex;
        [self requestData:[NSString stringWithFormat:@"%@%@%@",[Helper getIp],history_list_url,self.accouId] withPage:_pageIndex];
        [self performSelector:@selector(doneWithView:) withObject:refreshView afterDelay:1.0];
        
    };
    _footer = footer;
}

- (void)doneWithView:(MJRefreshBaseView *)refreshView
{
    // 刷新表格
    [self.tableView reloadData];
    // 调用endRefreshing结束刷新状态
    [refreshView endRefreshing];
}

//设置行高
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_reservationArray count]==0) {
        self.tableView.separatorStyle = NO;
    }else{
        self.tableView.separatorStyle = YES;
    }
    return [_reservationArray count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{  
    static NSString *cellIdentify=@"historyCell";
    HistoryCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[HistoryCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
    }
    cell.reservationModel=_reservationArray[indexPath.row];

    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    [Helper arrowStyle:cell];

    return cell;
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    Reservation *n = [_reservationArray objectAtIndex:row];
    if (n)
    {
        [self pushHistoryDetail:n andNavController:self.navigationController];
    }
}

//弹出历史信息页面
- (void)pushHistoryDetail:(Reservation *)model andNavController:(UINavigationController *)navController
{
    AppraiseViewController *appraise = [[AppraiseViewController alloc] init];
    appraise.merchartID = model.merchantId;
    appraise.accouId = self.accouId;
    appraise.rid=model.id;
    appraise.isCommented=model.isCommented;
    [navController pushViewController:appraise animated:YES];
}

-(void)requestData:(NSString *)urlStr withPage:(int)page
{   _loadFlag=YES;
    if ([Helper isConnectionAvailable]){
        NSString *str1= [NSString stringWithFormat:@"%@&page=%d", urlStr, page];
        NSString *response =[QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                for(int i=0; i < [jsonObjects count];i++ ){
                    Reservation *model=[[Reservation alloc]init];
                    model.name=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantName"];
                    model.merchantId=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantId"];
                    model.id=[[jsonObjects objectAtIndex:i] objectForKey:@"id"];
                    model.isCommented=[[[jsonObjects objectAtIndex:i] objectForKey:@"isCommented"] boolValue];
                    model.imgUrl=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantImage"];
                    
                    [_reservationArray addObject:model];
                }
            }
        }
    }//如果没有网络连接
    else
    {
        _loadFlag = NO;
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

-(void)dealloc
{
    _footer=nil;
}
@end
