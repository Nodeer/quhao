//
//  CurrentViewController.m
//  quHaoIos
//
//  Created by sam on 13-10-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CurrentViewController.h"

@interface CurrentViewController ()

@end

@implementation CurrentViewController

-(void)loadNavigationItem
{
    self.tabBarItem.title=@"当前取号情况";
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //添加上面的导航
    [self loadNavigationItem];
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
    _reloading = NO;
    pageIndex=1;
    [self requestData:[NSString stringWithFormat:@"%@%@%@",[Helper getIp],currentMerchant_url,self.accouId] withPage:pageIndex];
    [self addFooter];
}

//上拉加载更多
- (void)addFooter
{
    MJRefreshFooterView *footer = [MJRefreshFooterView footer];
    footer.scrollView = self.tableView;
    footer.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        prevItemCount = [_merchartsArray count];
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

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

//设置行高
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_merchartsArray count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify=@"cell";
    HomeCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[HomeCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
    }
    cell.merchartModel=_merchartsArray[indexPath.row];
    [Helper arrowStyle:cell];
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    return cell;
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    MerchartModel *n = [_merchartsArray objectAtIndex:row];
    if (n)
    {
        [self pushCurrentDetail:n andNavController:self.navigationController];
    }
}

//弹出商家详细页面
- (void)pushCurrentDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController
{
    CurrentDetailController *current = [[CurrentDetailController alloc] init];
    current.merchartID = model.id;
    current.accountID = self.accouId;
    [navController pushViewController:current animated:YES];
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
                [self addAfterInfo:jsonObjects];
            }
        }
    }//如果没有网络连接
    else
    {
        loadFlag = NO;
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//上拉刷新增加数据
-(void)addAfterInfo:(NSArray *) objects
{
    for(int i=0; i < [objects count];i++ ){
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name=[[objects objectAtIndex:i] objectForKey:@"merchantName"];
        model.averageCost=[[[objects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id=[[objects objectAtIndex:i] objectForKey:@"merchantId"];
        model.imgUrl=[[objects objectAtIndex:i] objectForKey:@"merchantImage"];
        [_merchartsArray addObject:model];
    }
}
@end
