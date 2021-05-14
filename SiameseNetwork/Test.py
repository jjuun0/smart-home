import torchvision.datasets as dset
from SiameseNetwork.Dataset import SiameseNetworkDataset
import torchvision.transforms as transforms
from torch.utils.data import DataLoader
import torch
import matplotlib.pyplot as plt
import numpy as np
from torch.autograd import Variable
import torch.nn.functional as F
import torchvision.utils
from SiameseNetwork.Network import SiameseNetwork
from SiameseNetwork.Train import MODELNAME


def imshow(img, text=None, should_save=False):
    npimg = img.numpy()
    plt.axis("off")
    if text:
        plt.text(75, 8, text, style='italic', fontweight='bold',
                 bbox={'facecolor': 'white', 'alpha': 0.8, 'pad': 10})

    # PyTorch 는 이미지 데이터셋을 [Batch Size, Channel, Width, Height] 순서대로 저장
    # -> matplotlib 로 출력하기 위해서는 각 이미지를 [Width, Height, Channel] 형태로 변경
    plt.imshow(np.transpose(npimg, (1, 2, 0)))
    plt.show()


testing_dir = "../img/test/"


def test():
    net = SiameseNetwork()
    net.load_state_dict(torch.load('./'+MODELNAME))
    net.eval()
    net.cuda()

    test_dataset_folder = dset.ImageFolder(root=testing_dir)
    siamese_test_dataset = SiameseNetworkDataset(dataPath=test_dataset_folder,
                                            transform=transforms.Compose([transforms.Resize((100, 100)),
                                                                          transforms.ToTensor()]),
                                            should_invert=False)


    test_dataloader = DataLoader(siamese_test_dataset, num_workers=0, batch_size=1, shuffle=True)
    dataiter = iter(test_dataloader)
    x0, _, _ = next(dataiter)

    for i in range(20):
        _, x1, label2 = next(dataiter)
        concatenated = torch.cat((x0, x1), 0)

        output1, output2 = net(Variable(x0).cuda(), Variable(x1).cuda())
        euclidean_distance = F.pairwise_distance(output1, output2)
        imshow(torchvision.utils.make_grid(concatenated), 'Dissimilarity: {:.2f}'.format(euclidean_distance.item()))
