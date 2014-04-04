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
@synthesize searchBar;

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    _allCount = 0;
    _results=[[NSMutableArray alloc]initWithCapacity:20];
    [super viewDidLoad];
    self.navigationItem.title = @"搜 索";
    self.view.backgroundColor = [Helper getBackgroundColor];
    self.tableResult.backgroundColor = [Helper getBackgroundColor];
    _results = [[NSMutableArray alloc] initWithCapacity:20];
    
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    [searchBar becomeFirstResponder];
}

- (void)viewDidUnload
{
    [self setTableResult:nil];
    self.searchBar = nil;
    [super viewDidUnload];
}
- (void)clickToHome:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText
{
    if (self.searchBar.text.length == 0) {
        return;
    }
    //[searchBar resignFirstResponder];
    //清空
    [self clear];
    [self doSearch];
}

-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar
{
    self.searchBar.text = @"";
    [self.searchBar resignFirstResponder];
}


-(void)doSearch
{
    _isLoading = YES;
    //请求数据 暂时未分页
    //url含有中文先进行编码
    NSString *str=[NSString stringWithFormat:@"%@%@",[Helper getIp],@"/search?name="];
    str=[[str stringByAppendingString:self.searchBar.text]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSURL *url = [NSURL URLWithString:str];
    
    //[self._searchBar resignFirstResponder];
    self.tableResult.hidden = NO;
    _isLoading = NO;
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
    if (!_results) {
        _isLoadOver = YES;
        [self.tableResult reloadData];
        return;
    }
    [_results removeAllObjects];
    for(int i=0; i < [jsonObjects count];i++ ){
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name=[[jsonObjects objectAtIndex:i] objectForKey:@"name"];
        model.averageCost=[[[jsonObjects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id=[[jsonObjects objectAtIndex:i] objectForKey:@"id"];
        [_results addObject:model];
        
    }
    if (_results.count < 20) {
        _isLoadOver = YES;
    }
    [self.tableResult reloadData];
}
-(void)clear
{
    [_results removeAllObjects];
    [self.tableResult reloadData];
    _isLoading = NO;
    _isLoadOver = NO;
    _allCount = 0;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (_isLoadOver) {
        return _results.count == 0 ? 1 : _results.count;
    }
    else
        return _results.count + 1;
}
-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_isLoadOver) {
        return _results.count == 0 ? 62 : 50;
    }
    else
    {
        return indexPath.row < _results.count ? 50 : 62;
    }
}

-(void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_results.count > 0)
    {
        if (indexPath.row < _results.count)
        {
            UITableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:@"NormalCellIdentifier"];
            if (!cell) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"NormalCellIdentifier"];
                [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
                cell.textLabel.font = [UIFont boldSystemFontOfSize:15.0];
            }
            MerchartModel * s = [_results objectAtIndex:indexPath.row];
            cell.textLabel.text = s.name;
            [Helper arrowStyle:cell];
            
            return cell;
        }
        else
        {
            return [[DataSingleton Instance] getLoadMoreCell:tableView andIsLoadOver:_isLoadOver andLoadOverString:@"搜索完毕" andLoadingString:(_isLoading ? @"" : @"") andIsLoading:_isLoading];
        }
    }
    else
    {
            return [[DataSingleton Instance] getLoadMoreCell:tableView andIsLoadOver:_isLoadOver andLoadOverString:@"查无结果" andLoadingString:(_isLoading ? @"" : @"") andIsLoading:_isLoading];
    }
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.searchBar resignFirstResponder];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = indexPath.row;
    if (row >= _results.count)
    {
        if (!_isLoading && !_isLoadOver)
        {
            [self performSelector:@selector(doSearch)];
        }
    }
    else
    {
        MerchartModel * s = [_results objectAtIndex:row];
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

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}
@end
