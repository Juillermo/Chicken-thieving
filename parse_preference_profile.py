import os
import glob
import xml.etree.ElementTree as ElementTree

os.chdir('..//../genius/etc/templates/ANAC2015/group8-holiday')
profiles = glob.glob('*Profile*')

for profile_file in profiles:
    et = ElementTree.parse(profile_file).getroot()

    if e.findall('discount_factor')[0].get('value') == '1.0' and e.findall('reservation')[0].get('value') == '0.0':
        for issue in e.findall('issue'):
            if issue.get('vtype') == 'discrete':

    else:
        print("Rejected, has discount factor")

