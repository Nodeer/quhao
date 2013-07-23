//
//  WithiterFirstViewController.h
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ASIAuthenticationDialog.h"
#import "ASICacheDelegate.h"
#import "ASIDataCompressor.h"
#import "ASIDataDecompressor.h"
#import "ASIDownloadCache.h"
#import "ASIFormDataRequest.h"
#import "ASIHTTPRequest.h"
#import "ASIHTTPRequestConfig.h"
#import "ASIHTTPRequestDelegate.h"
#import "ASIInputStream.h"
#import "ASINetworkQueue.h"
#import "ASIProgressDelegate.h"


@interface WithiterFirstViewController : UIViewController{
    UILabel *categoryLabel1;
    UISearchBar *searchBar1;
}

@property (nonatomic, retain)
    IBOutlet UILabel *categoryLabel1;
@property (nonatomic, retain)
    IBOutlet UISearchBar *searchBar1;

-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar;

-(void)testJsonParser: (NSString *) jsonString;

@end
