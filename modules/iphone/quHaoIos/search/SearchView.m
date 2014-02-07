//
//  AppDelegate.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "SearchView.h"
#import "Helper.h"
#import "DataSingleton.h"
#import "MerchartModel.h"
#import "ASIHTTPRequest.h"
#import "SBJson.h"
#import "HomeCell.h"

@implementation SearchView
@synthesize tableResult;
@synthesize _searchBar;

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    allCount = 0;
    [super viewDidLoad];
    self.navigationItem.title = @"搜 索";
    self.view.backgroundColor = [Helper getBackgroundColor];
    self.tableResult.backgroundColor = [Helper getBackgroundColor];
    results = [[NSMutableArray alloc] initWithCapacity:20];
    
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    [_searchBar becomeFirstResponder];
}

- (void)viewDidUnload
{
    [self setTableResult:nil];
    self._searchBar = nil;
    [super viewDidUnload];
}
- (void)clickToHome:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}


// 搜索
-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
    if (_searchBar.text.length == 0) {
        return;
    }
    [searchBar resignFirstResponder];
    //清空
    [self clear];
    [self doSearch];
}
-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar
{
    searchBar.text = @"";
    [searchBar resignFirstResponder];
}


-(void)doSearch
{
    isLoading = YES;
    //请求数据 暂时未分页
    //url含有中文先进行编码
    NSString *str=[NSString stringWithFormat:@"%@%@",[Helper getIp],@"/search?name="];
    str=[[str stringByAppendingString:_searchBar.text]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSURL *url = [NSURL URLWithString:str];
    
    [self._searchBar resignFirstResponder];
    self.tableResult.hidden = NO;
    isLoading = NO;
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
        //        NSLog(@"%@", response);
    }
    
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSArray *jsonObjects = [jsonParser objectWithString:response error:&error];
    results=[[NSMutableArray alloc]initWithCapacity:[jsonObjects count]];
    if (!results) {
        isLoadOver = YES;
        [self.tableResult reloadData];
        return;
    }

    for(int i=0; i < [jsonObjects count];i++ ){
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name=[[jsonObjects objectAtIndex:i] objectForKey:@"name"];
        model.averageCost=[[[jsonObjects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id=[[jsonObjects objectAtIndex:i] objectForKey:@"id"];
        [results addObject:model];
        
    }
    if (results.count < 20) {
        isLoadOver = YES;
    }
    [results addObjectsFromArray:results];
    [self.tableResult reloadData];
}
-(void)clear
{
    [results removeAllObjects];
    [self.tableResult reloadData];
    isLoading = NO;
    isLoadOver = NO;
    allCount = 0;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (isLoadOver) {
        return results.count == 0 ? 1 : results.count;
    }
    else
        return results.count + 1;
}
-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (isLoadOver) {
        return results.count == 0 ? 62 : 50;
    }
    else
    {
        return indexPath.row < results.count ? 50 : 62;
    }
}
-(void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [Helper getCellBackgroundColor];
}
-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (results.count > 0)
    {
        if (indexPath.row < results.count)
        {
            UITableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:@"NormalCellIdentifier"];
            if (!cell) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"NormalCellIdentifier"];
                
            }
            MerchartModel * s = [results objectAtIndex:indexPath.row];
            cell.textLabel.font = [UIFont boldSystemFontOfSize:15.0];
            cell.textLabel.text = s.name;
            cell.textLabel.textColor=[UIColor redColor];

           // if (self.segmentSearch.selectedSegmentIndex != 0)
            if(1==0)
            {
              //  cell.detailTextLabel.text = [NSString stringWithFormat:@"%@ 发表于 %@", s.author, s.pubDate];
            }
            else
            {
                cell.detailTextLabel.text = @"";
            }
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            return cell;
        }
        else
        {
            return [[DataSingleton Instance] getLoadMoreCell:tableView andIsLoadOver:isLoadOver andLoadOverString:@"搜索完毕" andLoadingString:(isLoading ? @"" : @"") andIsLoading:isLoading];
        }
    }
    else
    {
            return [[DataSingleton Instance] getLoadMoreCell:tableView andIsLoadOver:isLoadOver andLoadOverString:@"查无结果" andLoadingString:(isLoading ? @"" : @"") andIsLoading:isLoading];
    }
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self._searchBar resignFirstResponder];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = indexPath.row;
    if (row >= results.count)
    {
        if (!isLoading && !isLoadOver)
        {
            [self performSelector:@selector(doSearch)];
        }
    }
    else
    {
        MerchartModel * s = [results objectAtIndex:row];
        if (s)
        {
            [self pushSearchDetail:s andNavController:self.navigationController];
        }
    }
}

//弹出商家详细页面
- (void)pushSearchDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController
{
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = model.id;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    [navController pushViewController:mDetail animated:YES];
    
}
@end
