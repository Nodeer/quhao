//
//  CreditViewController.m
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CreditViewController.h"

@interface CreditViewController ()

@end

@implementation CreditViewController

-(void)loadNavigationItem
{   
    self.tabBarItem.title=@"积分消费情况";
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
    _creditArray = [[NSMutableArray alloc] initWithCapacity:0];
    _reloading = NO;
    pageIndex=1;
    [self requestData:[NSString stringWithFormat:@"%@%@?accountId=%@",[Helper getIp],credit_url,self.accouId] withPage:pageIndex];
    [self addFooter];
}

//上拉加载更多
- (void)addFooter
{
    MJRefreshFooterView *footer = [MJRefreshFooterView footer];
    footer.scrollView = self.tableView;
    footer.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        prevItemCount = [_creditArray count];
        ++pageIndex;
        [self requestData:[NSString stringWithFormat:@"%@%@%@",[Helper getIp],currentMerchant_url,self.accouId] withPage:pageIndex];
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
    return 70;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_creditArray count];
}
- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify=@"cell";
    CreditCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[CreditCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
    }
    cell.creditModel=_creditArray[indexPath.row];
    
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    cell.accessoryType = UITableViewCellAccessoryNone;
    
    return cell;
}

-(void)requestData:(NSString *)urlStr withPage:(int)page
{
    loadFlag=YES;
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
                    Credit *model=[[Credit alloc]init];
                    model.merchantName=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantName"];
                    model.merchantId=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantId"];
                    model.status=[[jsonObjects objectAtIndex:i] objectForKey:@"status"];
                    model.seatNumber=[[jsonObjects objectAtIndex:i] objectForKey:@"seatNumber"];
                    model.myNumber=[[jsonObjects objectAtIndex:i] objectForKey:@"myNumber"];
                    model.reservationId=[[jsonObjects objectAtIndex:i] objectForKey:@"reservationId"];
                    model.cost=[[[jsonObjects objectAtIndex:i] objectForKey:@"cost"] boolValue];
                    model.created=[[jsonObjects objectAtIndex:i] objectForKey:@"created"];
                    
                    [_creditArray addObject:model];
                }
            }
        }
    }//如果没有网络连接
    else
    {
        loadFlag = NO;
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

@end
